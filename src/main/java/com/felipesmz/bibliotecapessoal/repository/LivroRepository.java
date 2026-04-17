package com.felipesmz.bibliotecapessoal.repository;

import com.felipesmz.bibliotecapessoal.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findAllByUsuarioId(Long id);

    Optional<Livro> findByIdAndUsuarioId(Long id, Long usuarioId);
}
