/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.model.Mascota;
import com.example.demo.service.MascotaService;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

import java.io.OutputStream;

/**
 *
 * @author ANTONY
 */
@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public String listarMascotas(Model model) {
        List<Mascota> lista = mascotaService.listarMascotas();
        model.addAttribute("mascotas", lista);
        return "lista_mascotas";
    }

    @GetMapping("/nuevo")
    public String formularioMascota(Model model) {
        model.addAttribute("mascota", new Mascota());
        return "formulario_mascotas";
    }

    @PostMapping("/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota) {
        mascotaService.guardarMascota(mascota);
        return "redirect:/mascotas";
    }

    @GetMapping("/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.obtenerMascotaPorId(id);
        if (mascota != null) {
            model.addAttribute("mascota", mascota);
            return "mascotas/formulario";
        }
        return "redirect:/mascotas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return "redirect:/mascotas";
    }

    // Exportar a Excel
    @GetMapping("/exportar/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=mascotas.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Mascotas");

            // Cabecera
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Tipo", "Raza", "Edad", "Sexo", "Dueño", "Teléfono"};
            for (int i = 0; i < headers.length; i++) {
                //Cell cell = header.createCell(i);
                Cell cell = (Cell) header.createCell(i);
                cell.setCellValue(headers[i]);

            }

            // Datos
            List<Mascota> mascotas = mascotaService.listarMascotas();
            int rowNum = 1;
            for (Mascota mascota : mascotas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(mascota.getId());
                row.createCell(1).setCellValue(mascota.getNombre());
                row.createCell(2).setCellValue(mascota.getTipo());
                row.createCell(3).setCellValue(mascota.getRaza());
                row.createCell(4).setCellValue(mascota.getEdad());
                row.createCell(5).setCellValue(mascota.getSexo());
                row.createCell(6).setCellValue(mascota.getNombreDueño());
                row.createCell(7).setCellValue(mascota.getTelefonoDueño());
            }

            workbook.write(response.getOutputStream());
        }
    }

    // Exportar a PDF
    @GetMapping("/exportar/pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=mascotas.pdf");

        try (OutputStream out = response.getOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            try (Document document = new Document(pdfDoc)) {

                Table table = new Table(new float[]{1, 2, 2, 2, 1, 1, 2, 2});
                table.setWidthPercent(100); 


                String[] headers = {"ID", "Nombre", "Tipo", "Raza", "Edad", "Sexo", "Dueño", "Teléfono"};
                for (String header : headers) {
                    table.addCell(new Cell().add(header).setBold());
                }


                List<Mascota> mascotas = mascotaService.listarMascotas();
                for (Mascota mascota : mascotas) {
                    table.addCell(String.valueOf(mascota.getId()));
                    table.addCell(mascota.getNombre());
                    table.addCell(mascota.getTipo());
                    table.addCell(mascota.getRaza());
                    table.addCell(mascota.getEdad());
                    table.addCell(mascota.getSexo());
                    table.addCell(mascota.getNombreDueño());
                    table.addCell(mascota.getTelefonoDueño());
                }

// Agregar la tabla al documento
                document.add(table);
            }
        }
    }
}
