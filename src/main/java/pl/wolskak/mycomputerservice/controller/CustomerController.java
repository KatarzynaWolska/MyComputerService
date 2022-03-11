package pl.wolskak.mycomputerservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.wolskak.mycomputerservice.model.Repair;
import pl.wolskak.mycomputerservice.service.CustomerService;
import pl.wolskak.mycomputerservice.utils.UserUtils;

import java.util.List;

@AllArgsConstructor
@Controller
public class CustomerController {

    private CustomerService customerService;

    @GetMapping("/customer")
    public String homeCustomerPage(Model model) {
        List<Repair> customersFinishedRepairs = customerService.findCustomersFinishedRepairsToNotify(UserUtils.getLoggedInUserName());
        model.addAttribute("finishedRepairs", customersFinishedRepairs);
        return "home";
    }

    @GetMapping("/customer/invoices")
    public String getCustomerRepairs(Model model) {
        List<Repair> customersRepairs = customerService.findCustomersFixedRepairs(UserUtils.getLoggedInUserName());
        model.addAttribute("customerRepairs", customersRepairs);
        return "customer_invoices_list";
    }

    @GetMapping("/customer/downloadInvoice/{rid}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable("rid") Integer repairId) {
        byte [] invoiceByte = customerService.findRepairInvoice(repairId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = "invoice.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(invoiceByte, headers, HttpStatus.OK);
    }

    @GetMapping("/customer/confirmRepairNotification/{rid}")
    public String confirmRepairNotification(@PathVariable("rid") Integer repairId) {
        customerService.confirmRepairNotification(repairId);
        return "redirect:/customer";
    }
}
