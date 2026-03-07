package com.microservice.controller;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.microservice.modal.Employee;
import com.microservice.service.EmployeeServiceImpl;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeServiceImpl;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("allemplist", employeeServiceImpl.getAllEmployee());
        return "index";
    }

    @GetMapping("/addnew")
    public String addNewEmployee(Model model) {
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "newemployee";
    }

    @InitBinder
    // disable binding of raw multipart data directly into the `avatar` field
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("avatar");
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {
        try {
            if (avatarFile != null && !avatarFile.isEmpty()) {
                employee.setAvatar(avatarFile.getBytes());
            } else if (employee.getId() != 0) {
                // preserve existing avatar when updating and no new file uploaded
                Employee existing = employeeServiceImpl.getById(employee.getId());
                employee.setAvatar(existing.getAvatar());
            }
        } catch (Exception e) {
            // ignore file errors and continue saving without avatar
        }

        employeeServiceImpl.save(employee);
        return "redirect:/";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String updateForm(@PathVariable(value = "id") long id, Model model) {
        Employee employee = employeeServiceImpl.getById(id);
        model.addAttribute("employee", employee);
        return "update";
    }

    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> avatar(@PathVariable("id") long id) {
        try {
            Employee employee = employeeServiceImpl.getById(id);
            if (employee == null || employee.getAvatar() == null) {
                return ResponseEntity.notFound().build();
            }
            byte[] image = employee.getAvatar();
            String contentType = null;
            try (java.io.ByteArrayInputStream is = new java.io.ByteArrayInputStream(image)) {
                contentType = java.net.URLConnection.guessContentTypeFromStream(is);
            } catch (Exception ex) {
                contentType = null;
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(image.length);
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Employees");
            int rowIdx = 0;
            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Created Date");
            header.createCell(4).setCellValue("Role");
            header.createCell(5).setCellValue("Status");

            for (com.microservice.modal.Employee e : employeeServiceImpl.getAllEmployee()) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(e.getId());
                r.createCell(1).setCellValue(e.getName() == null ? "" : e.getName());
                r.createCell(2).setCellValue(e.getEmail() == null ? "" : e.getEmail());
                r.createCell(3).setCellValue(e.getCreatedDate() == null ? "" : e.getCreatedDate().toString());
                r.createCell(4).setCellValue(e.getRole() == null ? "" : e.getRole());
                r.createCell(5).setCellValue(e.getStatus() == null ? "" : e.getStatus());
            }

            for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "employees.xlsx");
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/deleteEmployee/{id}")
    public String deleteThroughId(@PathVariable(value = "id") long id) {
        employeeServiceImpl.deleteViaId(id);
        return "redirect:/";

    }
}