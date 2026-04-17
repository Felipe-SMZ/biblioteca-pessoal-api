package com.felipesmz.bibliotecapessoal.repository;

import com.felipesmz.bibliotecapessoal.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findAllByUsuarioId(Long id);

}
