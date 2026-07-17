package com.minimarket.service.impl;

import com.minimarket.dto.categoria.CategoriaRequestDTO;
import com.minimarket.dto.categoria.CategoriaResponseDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.service.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<CategoriaResponseDTO> findAll() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias.stream()
                .map(categoria -> new CategoriaResponseDTO(categoria.getId(), categoria.getNombre()))
                .toList();
    }

    @Override
    public CategoriaResponseDTO findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria no encontrda"));
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNombre());
    }

    @Override
    public CategoriaResponseDTO crear(CategoriaRequestDTO request) {
        if(!categoriaRepository.existsByNombre(request.nombre())){
            Categoria categoria = new Categoria();
            categoria.setNombre(request.nombre());
            Categoria guardada = categoriaRepository.save(categoria);
            return new CategoriaResponseDTO(guardada.getId(), guardada.getNombre());
        }
        throw new RuntimeException("Ya existe una categoria con el nombre: " + request.nombre());
    }

    @Override
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        if (categoriaRepository.existsByNombreAndIdNot(request.nombre(), id)) {
            throw new RuntimeException("Ya existe otra categoria con el nombre: " + request.nombre());
        }

        categoria.setNombre(request.nombre());
        Categoria actualizada = categoriaRepository.save(categoria);

        return new CategoriaResponseDTO(actualizada.getId(), actualizada.getNombre());
    }


    @Override
    public void deleteById(Long id) {
        Categoria categoria = categoriaRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        if(!categoria.getProductos().isEmpty()){
            throw new RuntimeException("No se puede eliminar: la categoria con id " + id + " tiene productos asociados");
        }
        categoriaRepository.deleteById(id);
    }
}
