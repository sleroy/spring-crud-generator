import com.byoskill.tools.example.Payment;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SaltServiceGeneratorTest {

    @Test
    public void testGeneration() throws IOException {

        GenerationServiceOptions generationServiceOptions = new GenerationServiceOptions();
        generationServiceOptions.setServiceClassName("PaymentServiceRest");
        generationServiceOptions.setServicePackageName("ch.demo.generation.services");
        generationServiceOptions.setOutputFolder("src/test/java");
        //generationServiceOptions.setRepositoryClassName(PaymentRepository.class.getCanonicalName());
        generationServiceOptions.setRestPath("/api/v1/payments");
        generationServiceOptions.setEntityClassName(Payment.class.getName());
        generationServiceOptions.setEntityDtoTypeName("ch.demo.generation.model.PaymentDto");
        generationServiceOptions.setEntityConverterClassName("ch.demo.generation.converters.PaymentConverter");
        generationServiceOptions.setPrimaryKeyType(Long.class.getCanonicalName());

        SaltServiceGenerator saltServiceGenerator = new SaltServiceGenerator(generationServiceOptions);
        saltServiceGenerator.generate();


    }


}