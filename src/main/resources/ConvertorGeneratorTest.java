import com.byoskill.tools.example.Payment;
import org.junit.jupiter.api.Test;

public class ConvertorGeneratorTest {

    @Test
    public void generateRomeroPayment() throws Exception {


        EntityDtoGeneratorOptions entityDtoGeneratorOptions = new EntityDtoGeneratorOptions();

        entityDtoGeneratorOptions.setOutputFolder("src/test/java");
        entityDtoGeneratorOptions.setEntityClassName(Payment.class);
        entityDtoGeneratorOptions.setPrimaryKeyType(Long.class.getCanonicalName());
        entityDtoGeneratorOptions.setModelPackageName("ch.demo.generation.model");

        EntityDtoGenerator entityDtoGenerator = new EntityDtoGenerator(entityDtoGeneratorOptions);
        entityDtoGenerator.generate();


        for (DtoMapping dtoMapping : entityDtoGenerator.getGeneratedDtoTypes()) {

            ConvertorGeneratorOptions options = new ConvertorGeneratorOptions();
            options.setConvertPackageName("ch.demo.generation.converters");
            options.setOutputFolder("src/test/java");
            options.setDtoMapping(dtoMapping);
            options.setConverterName("PaymentConverter");

            ConvertorGenerator convertorGenerator = new ConvertorGenerator(options);
            convertorGenerator.generate();

        }
    }
}