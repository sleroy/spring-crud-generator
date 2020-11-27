import ch.salt.paymentcenter.paybylink.dao.PaymentRepository;

import java.io.IOException;


public class SaltRestApiGenerator {
    private final RepositoryMetadataScanner repositoryMetadataScanner;
    private final GenerationOptions         options;

    public SaltRestApiGenerator(RepositoryMetadataScanner repositoryMetadataScanner, GenerationOptions options) {

        this.repositoryMetadataScanner = repositoryMetadataScanner;
        this.options                   = options;
    }

    public void generate() throws IOException {

        generateService();
        generateProcessors();
        generateDtos();
        generateConverters();

    }

    private void generateService() throws IOException {
        GenerationServiceOptions generationServiceOptions = new GenerationServiceOptions();
        generationServiceOptions.serviceClassName = "PaymentServiceRest";
        generationServiceOptions.setServicePackageName("ch.salt.paymentcenter.paybylink.services");
        generationServiceOptions.setRepositoryClassName(PaymentRepository.class.getCanonicalName()); // FQN
        generationServiceOptions.setRestPath(this.options.getRestPath() + "/" + "payments");
        generationServiceOptions.setOutputFolder("/tmp/PaymentServiceRest.java");

        SaltServiceGenerator saltServiceGenerator = new SaltServiceGenerator(generationServiceOptions);
        saltServiceGenerator.generate();


    }

    private void generateProcessors() {

    }

    private void generateDtos() {



    }

    private void generateConverters() {

    }
}
