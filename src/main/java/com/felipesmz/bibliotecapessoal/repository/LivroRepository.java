package com.felipesmz.bibliotecapessoal.repository;

import com.felipesmz.bibliotecapessoal.model.Livro;
import com.felipesmz.bibliotecapessoal.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findAllByUsuarioId(Long id);

    Optional<Livro> findByIdAndUsuarioId(Long id, Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    long countByUsuarioIdAndStatus(Long usuarioId, Status status);

    @Query("SELECT AVG(l.avaliacao) FROM Livro l WHERE l.usuario.id = :usuarioId AND l.avaliacao IS NOT NULL")
    Double mediaAvaliacao(Long usuarioId);

    @Query("SELECT SUM(l.paginasLidas) FROM Livro l WHERE l.usuario.id = :usuarioId")
    Integer somaPaginasLidas(Long usuarioId);
}
