package com.API_Foro.Challenge.Controller;

import com.API_Foro.Challenge.DTO.DatosActualizarTopicoDTO;
import com.API_Foro.Challenge.DTO.DatosRespuestaTopico;
import com.API_Foro.Challenge.DTO.RegistroTipocoDTO;
import com.API_Foro.Challenge.Repository.AutorRepository;
import com.API_Foro.Challenge.Repository.CursoRepository;
import com.API_Foro.Challenge.Repository.TopicoRepository;
import com.API_Foro.Challenge.domain.Topico;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.beans.Transient;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    TopicoRepository repositoryTopico;

    @Autowired
    AutorRepository autorRepository;

    @Autowired
    CursoRepository cursoRepository;
    @Autowired
    private TopicoRepository topicoRepository;

    @PostMapping
    public ResponseEntity<DatosRespuestaTopico> registrarTopico(@RequestBody @Valid RegistroTipocoDTO registroTipocoDTO,
                                                                UriComponentsBuilder uriComponentsBuilder) {

        var autor = autorRepository.findById(registroTipocoDTO.autorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Autor no encontrado"));

        var curso = cursoRepository.findById(registroTipocoDTO.cursoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        var topicoRepetido = topicoRepository.buscsarPorMensajeTitulo(registroTipocoDTO.titulo(),registroTipocoDTO.mensaje());
        if(topicoRepetido.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tópico duplicado: ya existe con ese título y mensaje");
        }

        var topico = new Topico(registroTipocoDTO, autor, curso);
        repositoryTopico.save(topico);

        var respuesta = new DatosRespuestaTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus().name(),
                autor.getId(),
                curso.getId()
        );

        URI url = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(url).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<Page<DatosRespuestaTopico>> listarTopicos(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC)Pageable pageable){

        Page<DatosRespuestaTopico> respuesta = repositoryTopico.findAll(pageable)
                .map(topico -> new DatosRespuestaTopico(
                        topico.getId(),
                        topico.getTitulo(),
                        topico.getMensaje(),
                        topico.getFechaCreacion(),
                        topico.getStatus().name(),
                        topico.getAutor().getId(),
                        topico.getCurso().getId()
                ));

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("{id}")
    public ResponseEntity<DatosRespuestaTopico> mostrarTopico(@PathVariable Long id){
        Topico topico = repositoryTopico.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado"));


        var datosTopico = new DatosRespuestaTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus().name(),
                topico.getAutor().getId(),
                topico.getCurso().getId()
        );
        return ResponseEntity.ok(datosTopico);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity actualizarTopico(
            @PathVariable Long id,
            @RequestBody @Valid DatosActualizarTopicoDTO datosActualizar)
    {
        var topicoOptional = repositoryTopico.findById(id);
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var topico = topicoOptional.get();
        //verifica si hay cambios
        boolean tituloSinCambio = datosActualizar.titulo() == null || datosActualizar.titulo().equals(topico.getTitulo());
        boolean mensajeSinCambio = datosActualizar.mensaje() == null || datosActualizar.mensaje().equals(topico.getMensaje());
        boolean statusSinCambio = datosActualizar.status() == null || datosActualizar.status().equals(topico.getStatus());

        boolean sinCambios = tituloSinCambio && mensajeSinCambio && statusSinCambio;

        if (sinCambios) {
            //si no hubo cambios reales devuelve estado 200 con el mismo contenido
            var respuestaSinCambios = new DatosRespuestaTopico(
                    topico.getId(),
                    topico.getTitulo(),
                    topico.getMensaje(),
                    topico.getFechaCreacion(),
                    topico.getStatus().name(),
                    topico.getAutor().getId(),
                    topico.getCurso().getId()
            );
            return ResponseEntity.ok(respuestaSinCambios);
        }

        //verifica duplciados si hubo cambios en alguno
        if (!tituloSinCambio && !mensajeSinCambio) {
            var repetido = repositoryTopico.buscsarPorMensajeTitulo(
                    datosActualizar.titulo(),
                    datosActualizar.mensaje());

            if (repetido.isPresent() && !repetido.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro tópico con ese título y mensaje");
            }
        }


        topico.actualizar(datosActualizar);


        var respuesta = new DatosRespuestaTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus().name(),
                topico.getAutor().getId(),
                topico.getCurso().getId()
        );

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity eliminarTopico(@PathVariable Long id){

        var topico = repositoryTopico.findById(id);
        if (topico.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tópico no encontrado"));
        }

        repositoryTopico.deleteById(topico.get().getId());

        return ResponseEntity.ok(Map.of("mensaje", "Tópico eliminado correctamente"));

    }
}
