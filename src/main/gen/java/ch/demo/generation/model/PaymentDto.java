


package ch.demo.generation.model;

/**
* Light DTO Entity without relationships for {link com.byoskill.tools.example.Payment }
*/
public class PaymentDto {
    private List<Credentials> credentials;
    /**
     * Instantiate a new DTO.
     */
    public PaymentDto() {
        super();
    }

        /**
         * Get the credentials field
         *
         * @returns the credentials property.
         */
        public List<Credentials> getCredentials() {
            return this.credentials;
        }

    
        /**
         * Sets the credentials field
         *
         * @param credentials the credentials
         */
        public List<Credentials> setCredentials(List<Credentials> credentials ) {
            this.credentials = credentials;
        }


}

