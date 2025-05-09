package com.example.junxtar.javaparser.view;

import com.example.junxtar.javaparser.dto.ParserDto;
import com.example.junxtar.javaparser.dto.TableDto;
import com.example.junxtar.javaparser.service.ParserService;
import com.example.junxtar.javaparser.service.TableRenderingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {
    private final TableRenderingService tableRenderingService;
    private final ParserService parserService;

    @GetMapping
    public String renderingTable(Model model) {
        List<TableDto> tables = tableRenderingService.getTables();
        model.addAttribute("tables", tables);
        return "/table";
    }

    @PostMapping("/parsing")
    @ResponseBody
    public ResponseEntity<Resource> parsing(@RequestBody ParserDto parserDto) {
        return parserService.parsing(parserDto);
    }
}

