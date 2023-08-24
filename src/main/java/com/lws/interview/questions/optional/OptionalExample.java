package com.lws.interview.questions.optional;


import lombok.Data;

import java.util.Optional;

@Data
class Account {
    private String number;
    private String fistName;
    private String lastName;

    public String getFullName() {
        return fistName + lastName;
    }
}

@Data
class UserDTO {
    private Long id;
    private Optional<String> name;
    private Optional<Integer> age;
}

@Data
//@Entity
//@DynamicUpdate
class UserEntity {

    private Long id;
    private String name;
    private Integer age;

}

public class OptionalExample {

    public static void main(String[] args) {

    }

    // case 1
    public static String getAccountFull(Account account) {
        // legacy code
        if (account != null
                && account.getFistName() != null
                && account.getLastName() != null) {
            return account.getFullName();
        }

        // java 8 optional code
//        return Optional.of(account).map(Account::getFullName).orElse("");

        return "";
    }


    //case 2
    public static void updateUserInfo(UserDTO updateUser) {
        Long id = Optional
                .of(updateUser)
                .map(UserDTO::getId)
                .orElseThrow(() -> new IllegalArgumentException("id can't be null"));

        UserEntity existingUser = getUserById(id).orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (updateUser.getName() != null) {
            existingUser.setName(updateUser.getName().orElseThrow(() -> new IllegalArgumentException("name can't be null")));
        }

        if (updateUser.getAge() != null) {
            existingUser.setAge(updateUser.getAge().orElseThrow(() -> new IllegalArgumentException("age can't be null")));
        }

        // update user
    }


    public static Optional<UserEntity> getUserById(Long id) {
        return null;
    }

}
