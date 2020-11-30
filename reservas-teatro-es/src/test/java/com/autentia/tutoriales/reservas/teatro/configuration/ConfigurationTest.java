package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandContext;
import org.junit.Test;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void givenFullConfigurationThenDependenciesInjected() {
        contextRunner.withUserConfiguration(
                RepresentacionConfiguration.class,
                ReservaConfiguration.class,
                ClienteConfiguration.class,
                PagoConfiguration.class,
                SagaConfiguration.class,
                HistoricoConfiguration.class).run(context -> {
            assertRepresentacionPublisher(context);
            assertRepresentacionDispatcher(context);
            assertReservaPublisher(context);
            assertReservaDispatcher(context);
            assertClientePublisher(context);
            assertClienteDispatcher(context);
            assertPagoPublisher(context);
            assertPagoDispatcher(context);
        });
    }

    private void assertRepresentacionPublisher(final AssertableApplicationContext context) {
        assertThat(context).getBean("representacionPublisher")
                .hasFieldOrPropertyWithValue("eventConsumers", Set.of(
                        context.getBean("representacionEventConsumer"),
                        context.getBean("representacionSaga"),
                        context.getBean("historicoEventConsumer")));
    }

    private void assertRepresentacionDispatcher(final AssertableApplicationContext context) {
        assertThat(context).getBean("representacionDispatcher")
                .extracting("context").isOfAnyClassIn(RepresentacionCommandContext.class);
    }

    private void assertReservaPublisher(final AssertableApplicationContext context) {
        assertThat(context).getBean("reservaPublisher")
                .hasFieldOrPropertyWithValue("eventConsumers", Set.of(
                        context.getBean("reservaEventConsumer"),
                        context.getBean("reservaSaga"),
                        context.getBean("historicoEventConsumer")));
    }

    private void assertReservaDispatcher(final AssertableApplicationContext context) {
        assertThat(context).getBean("reservaDispatcher")
                .extracting("context").isOfAnyClassIn(ReservaCommandContext.class);
    }

    private void assertClientePublisher(final AssertableApplicationContext context) {
        assertThat(context).getBean("clientePublisher")
                .hasFieldOrPropertyWithValue("eventConsumers", Set.of(
                        context.getBean("clienteEventConsumer"),
                        context.getBean("clienteSaga"),
                        context.getBean("historicoEventConsumer")));
    }

    private void assertClienteDispatcher(final AssertableApplicationContext context) {
        assertThat(context).getBean("clienteDispatcher")
                .extracting("context").isOfAnyClassIn(ClienteCommandContext.class);
    }

    private void assertPagoPublisher(final AssertableApplicationContext context) {
        assertThat(context).getBean("pagoPublisher")
                .hasFieldOrPropertyWithValue("eventConsumers", Set.of(
                        context.getBean("pagoEventConsumer"),
                        context.getBean("pagoSaga"),
                        context.getBean("historicoEventConsumer")));
    }

    private void assertPagoDispatcher(final AssertableApplicationContext context) {
        assertThat(context).getBean("pagoDispatcher")
                .extracting("context").isOfAnyClassIn(PagoCommandContext.class);
    }
}
