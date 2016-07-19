package pokemon.dto;

public class OathRequest {
    private String client_id;
    private String redirect_uri;
    private String client_secret;
    private String grant_type;
    private String code;

    private OathRequest(Builder builder) {
        this.client_id = builder.client_id;
        this.redirect_uri = builder.redirect_uri;
        this.client_secret = builder.client_secret;
        this.grant_type = builder.grant_type;
        this.code = builder.code;
    }

    public static Builder newoathRequest() {
        return new Builder();
    }


    public static final class Builder {
        private String client_id;
        private String redirect_uri;
        private String client_secret;
        private String grant_type;
        private String code;

        public Builder() {
        }

        public OathRequest build() {
            return new OathRequest(this);
        }

        public Builder client_id(String client_id) {
            this.client_id = client_id;
            return this;
        }

        public Builder redirect_uri(String redirect_uri) {
            this.redirect_uri = redirect_uri;
            return this;
        }

        public Builder client_secret(String client_secret) {
            this.client_secret = client_secret;
            return this;
        }

        public Builder grant_type(String grant_type) {
            this.grant_type = grant_type;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }
    }
}
