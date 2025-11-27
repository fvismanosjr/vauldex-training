register
    - firstname
    - lastname
    - birthdate (compute age based on this and make sure that age is greater than 5)
    - email (username)
    - password
    - confirm password (always equal to password)

login
    - email (username)
    - password

roles
    - id
    - name (ROLE_STUDENT, ROLE_ADMIN, ROLE_SUDO)
        ROLE_STUDENT
            - show student profile
            - show table of courses (optional)
        ROLE_ADMIN
            - show all options except for creating users and roles
        ROLE_SUDO
            - show all

users
    - id
    - username (email)
    - password (bcrypt)
    - role_id

students
    - id
    - firstname
    - lastname
    - birthdate
    - degree_id (optional)

degree
    - id
    - name
    - initial
    - description
    - degree_id

