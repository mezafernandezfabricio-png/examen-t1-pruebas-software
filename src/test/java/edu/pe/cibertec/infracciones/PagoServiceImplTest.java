package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    void cuandoPagoEsMismoDiaDeEmision_AplicaDescuento20YEstadoPagada() {
        Long multaId = 1L;
        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setMonto(500.00);
        multa.setFechaEmision(LocalDate.now());
        multa.setFechaVencimiento(LocalDate.now().plusDays(30));
        multa.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));

        pagoService.procesarPago(multaId);

        assertEquals(EstadoMulta.PAGADA, multa.getEstado());
        verify(pagoRepository, times(1)).save(any(Pago.class));
        verify(multaRepository, times(1)).save(multa);
    }

    @Captor
    private ArgumentCaptor<Pago> pagoCaptor;

    @Test
    void escenarioRecargo_VerificarConArgumentCaptor() {
        Long multaId = 2L;
        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setMonto(500.00);
        multa.setFechaEmision(LocalDate.now().minusDays(12));
        multa.setFechaVencimiento(LocalDate.now().minusDays(2));
        multa.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));

        pagoService.procesarPago(multaId);

        verify(pagoRepository, times(1)).save(pagoCaptor.capture());

        Pago pagoCapturado = pagoCaptor.getValue();

        assertEquals(75.00, pagoCapturado.getRecargo());
        assertEquals(0.00, pagoCapturado.getDescuentoAplicado());
        assertEquals(575.00, pagoCapturado.getMontoPagado());
    }
}