package pokemon.dto;

public class LoginRequest {
    private String username;
    private String password;
    private String _eventId;
    private String execution;
    private String lt;

    private LoginRequest(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this._eventId = builder._eventId;
        this.execution = builder.execution;
        this.lt = builder.lt;
    }

    public static Builder newLoginRequest() {
        return new Builder();
    }


    public static final class Builder {
        private String username;
        private String password;
        private String _eventId;
        private String execution;
        private String lt;

        public LoginRequest build() {
            return new LoginRequest(this);
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder _eventId(String _eventId) {
            this._eventId = _eventId;
            return this;
        }

        public Builder execution(String execution) {
            this.execution = execution;
            return this;
        }

        public Builder lt(String lt) {
            this.lt = lt;
            return this;
        }
    }
}
