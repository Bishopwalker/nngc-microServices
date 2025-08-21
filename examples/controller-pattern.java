// Example Spring Boot REST Controller Pattern
@RestController
@RequestMapping("/api/v1/example")
@CrossOrigin(origins = "*")
@Validated
public class ExampleController {

    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExampleDTO>> getExample(@PathVariable Long id) {
        try {
            ExampleDTO result = exampleService.findById(id);
            return ResponseEntity.ok(
                ApiResponse.<ExampleDTO>builder()
                    .success(true)
                    .message("Example retrieved successfully")
                    .data(result)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.<ExampleDTO>builder()
                    .success(false)
                    .message("Error retrieving example: " + e.getMessage())
                    .build()
            );
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExampleDTO>> createExample(
            @Valid @RequestBody ExampleDTO exampleDTO) {
        try {
            ExampleDTO result = exampleService.create(exampleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ExampleDTO>builder()
                    .success(true)
                    .message("Example created successfully")
                    .data(result)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.<ExampleDTO>builder()
                    .success(false)
                    .message("Error creating example: " + e.getMessage())
                    .build()
            );
        }
    }
}