public class User {

    private int id;
    private String name;
    @Email
    private String email;
    private int age;
    private boolean hasAge = false;

    private User(int id, String name, String email, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public static class Builder {
        private int id;
        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        private String name;
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        private String email;
        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        private int age;
        public Builder setAge(int age) {
            this.age = age;
            hasAge = true;
            return this;
        }

        public User build() {
            if (this.id == null) {
                throw new IllegalStateException("Required field 'id' is missing");
            }
            if (this.name == null) {
                throw new IllegalStateException("Required field 'name' is missing");
            }
            if (this.email == null) {
                throw new IllegalStateException("Required field 'email' is missing");
            }
            return new User(id, name, email, age);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

}
