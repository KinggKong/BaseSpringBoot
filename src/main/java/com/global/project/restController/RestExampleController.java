package com.global.project.restController;


import com.global.project.dto.ExampleDto;
import com.global.project.repository.querydslRepository.ExampleQuerydslRepository;
import com.global.project.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "03. EXAMPLE")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/example")
@RequiredArgsConstructor
public class RestExampleController {
    private final ExampleQuerydslRepository customerQuerydslRepository;

    @Operation(summary = "example", description = "example", tags = {"03. EXAMPLE"})
    @GetMapping
    public Page<ExampleDto> findByPage(Pageable page,
                                       @RequestParam(required = false) String textSearch){
        return customerQuerydslRepository.findByPage(page,textSearch);
    }
}
