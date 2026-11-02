package in.schemesathi.scheme;
import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1/schemes") public class SchemeController{private final SchemeService service;public SchemeController(SchemeService service){this.service=service;}@GetMapping public List<SchemeDtos.SchemeResponse> list(@RequestParam(required=false)String state,@RequestParam(required=false)String category){return service.list(state,category);}@GetMapping("/{id}")public SchemeDtos.SchemeResponse detail(@PathVariable UUID id){return service.detail(id);}}
