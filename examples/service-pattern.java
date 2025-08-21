// Example Service Layer Pattern
@Service
@Transactional
public class ExampleService implements ExampleInterface {

    private final ExampleRepository exampleRepository;

    public ExampleService(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @Override
    public ExampleDTO findById(Long id) {
        Example entity = exampleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Example not found with id: " + id));
        return convertToDTO(entity);
    }

    @Override
    public ExampleDTO create(ExampleDTO exampleDTO) {
        Example entity = convertToEntity(exampleDTO);
        Example savedEntity = exampleRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    public ExampleDTO update(Long id, ExampleDTO exampleDTO) {
        Example existingEntity = exampleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Example not found with id: " + id));
        
        updateEntityFromDTO(existingEntity, exampleDTO);
        Example updatedEntity = exampleRepository.save(existingEntity);
        return convertToDTO(updatedEntity);
    }

    @Override
    public void deleteById(Long id) {
        if (!exampleRepository.existsById(id)) {
            throw new EntityNotFoundException("Example not found with id: " + id);
        }
        exampleRepository.deleteById(id);
    }

    private ExampleDTO convertToDTO(Example entity) {
        return ExampleDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    private Example convertToEntity(ExampleDTO dto) {
        return Example.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .build();
    }

    private void updateEntityFromDTO(Example entity, ExampleDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setUpdatedAt(LocalDateTime.now());
    }
}