package com.example.florra_a;

public class ForgotPasswordResponse extends BaseResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String email;
        private String reset_token;
        private String message;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getReset_token() {
            return reset_token;
        }

        public void setReset_token(String reset_token) {
            this.reset_token = reset_token;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}