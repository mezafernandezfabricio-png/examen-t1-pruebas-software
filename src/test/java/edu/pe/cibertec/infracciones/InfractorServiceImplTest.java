package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfractorServiceImplTest {

    @Mock
    private InfractorRepository infractorRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    void cuandoInfractorTiene2VencidasY3Pagadas_NoSeBloquea() {
        Long id = 1L;
        Infractor infractor = new Infractor();
        infractor.setId(id);
        infractor.setBloqueado(false);

        when(infractorRepository.findById(id)).thenReturn(Optional.of(infractor));
        when(infractorRepository.contarVencidas(id)).thenReturn(2L);

        infractorService.VeriBloqueo(id);

        verify(infractorRepository, never()).save(any(Infractor.class));
    }





}
