package com.andyyu.powersupply.termination.controller;

import com.andyyu.powersupply.termination.domain.TerminationApplication;
import com.andyyu.powersupply.termination.domain.WorkflowStatus;
import com.andyyu.powersupply.termination.service.TerminationApplicationService;
import com.andyyu.powersupply.termination.service.WorkflowException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class TerminationPageController {

    private final TerminationApplicationService service;

    public TerminationPageController(TerminationApplicationService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/applications";
    }

    @GetMapping("/applications")
    public String list(Model model,
                       String keyword,
                       WorkflowStatus status) {
        var allItems = service.all();
        model.addAttribute("items", service.list(keyword, status));
        model.addAttribute("allItems", allItems);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", WorkflowStatus.values());
        model.addAttribute("acceptedCount", count(allItems, WorkflowStatus.ACCEPTED));
        model.addAttribute("settledCount", count(allItems, WorkflowStatus.FEE_SETTLED, WorkflowStatus.INFO_ARCHIVED, WorkflowStatus.CALLBACK_DONE, WorkflowStatus.ARCHIVED));
        model.addAttribute("archivedCount", count(allItems, WorkflowStatus.ARCHIVED));
        model.addAttribute("newApplication", new TerminationApplication());
        return "applications/list";
    }

    @PostMapping("/applications")
    public String create(@Valid @ModelAttribute("newApplication") TerminationApplication application,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            var allItems = service.all();
            model.addAttribute("items", allItems);
            model.addAttribute("allItems", allItems);
            model.addAttribute("statuses", WorkflowStatus.values());
            model.addAttribute("keyword", "");
            model.addAttribute("acceptedCount", count(allItems, WorkflowStatus.ACCEPTED));
            model.addAttribute("settledCount", count(allItems, WorkflowStatus.FEE_SETTLED, WorkflowStatus.INFO_ARCHIVED, WorkflowStatus.CALLBACK_DONE, WorkflowStatus.ARCHIVED));
            model.addAttribute("archivedCount", count(allItems, WorkflowStatus.ARCHIVED));
            return "applications/list";
        }
        TerminationApplication created = service.create(application);
        redirectAttributes.addAttribute("id", created.getId());
        redirectAttributes.addFlashAttribute("pageMessage", "已创建受理单 " + created.getApplicationNo());
        return "redirect:/applications/{id}";
    }

    @GetMapping("/applications/{id}")
    public String detail(@PathVariable Long id, Model model) {
        TerminationApplication application = service.get(id);
        model.addAttribute("terminationApplication", application);
        model.addAttribute("statuses", WorkflowStatus.values());
        return "applications/detail";
    }

    @PostMapping("/applications/{id}/dispatch")
    public String dispatch(@PathVariable Long id,
                           String assignedSurveyor,
                           RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.dispatch(id, assignedSurveyor), id, redirectAttributes, "已完成勘查派工");
    }

    @PostMapping("/applications/{id}/survey")
    public String survey(@PathVariable Long id,
                         String surveyResult,
                         String customerAddress,
                         RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.survey(id, surveyResult, customerAddress), id, redirectAttributes, "已完成现场勘查");
    }

    @PostMapping("/applications/{id}/contract-terminate")
    public String contractTerminate(@PathVariable Long id,
                                    String contractNo,
                                    RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.terminateContract(id, contractNo), id, redirectAttributes, "已完成合同终止");
    }

    @PostMapping("/applications/{id}/power-off")
    public String powerOff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.powerOff(id), id, redirectAttributes, "已完成停电");
    }

    @PostMapping("/applications/{id}/determine-fee")
    public String determineFee(@PathVariable Long id,
                               java.math.BigDecimal actualDays,
                               RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.determineFee(id, actualDays), id, redirectAttributes, "已完成费用确定");
    }

    @PostMapping("/applications/{id}/settle-fee")
    public String settleFee(@PathVariable Long id,
                            String voucherNo,
                            java.math.BigDecimal settledAmount,
                            java.math.BigDecimal arrearsAmount,
                            RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.settleFee(id, voucherNo, settledAmount, arrearsAmount), id, redirectAttributes, "已完成费用结清");
    }

    @PostMapping("/applications/{id}/archive-info")
    public String archiveInfo(@PathVariable Long id,
                              String archiveLocation,
                              RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.archiveInfo(id, archiveLocation), id, redirectAttributes, "已完成信息归档");
    }

    @PostMapping("/applications/{id}/callback")
    public String callback(@PathVariable Long id,
                           String callbackResult,
                           RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.callback(id, callbackResult), id, redirectAttributes, "已完成客户回访");
    }

    @PostMapping("/applications/{id}/archive")
    public String archive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return redirectWithMessage(() -> service.archive(id), id, redirectAttributes, "已完成最终归档");
    }

    private String redirectWithMessage(Action action,
                                       Long id,
                                       RedirectAttributes redirectAttributes,
                                       String successMessage) {
        try {
            action.run();
            redirectAttributes.addFlashAttribute("pageMessage", successMessage);
        } catch (WorkflowException ex) {
            redirectAttributes.addFlashAttribute("pageError", ex.getMessage());
        }
        redirectAttributes.addAttribute("id", id);
        return "redirect:/applications/{id}";
    }

    private long count(java.util.List<TerminationApplication> items, WorkflowStatus... statuses) {
        java.util.Set<WorkflowStatus> allowed = java.util.EnumSet.noneOf(WorkflowStatus.class);
        java.util.Collections.addAll(allowed, statuses);
        return items.stream().filter(item -> allowed.contains(item.getStatus())).count();
    }

    @FunctionalInterface
    private interface Action {
        void run();
    }
}
