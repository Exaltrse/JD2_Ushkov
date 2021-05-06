package com.ushkov;


import com.ushkov.repository.UserRepository;
import com.ushkov.repository.impl.UserRepositoryImpl;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepositoryImpl();
        // Find all users
//        for (User user : userRepository.findAll()) {
//            System.out.println(user);
//        }

        //Find one
//        try {
//            System.out.println(userRepository.findOne(100L));
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }

        //Save
//        User user = new User();
//        user.setName("Test");
//        user.setSurname("Save");
//        user.setLogin("test_save_2");
//        user.setWeight(110f);
//        user.setBirthDate(new Date(12000000L));
//
//        System.out.println(userRepository.save(user));

        //Update
//        try{
//            User user = userRepository.findAll().get(0);
//            user.setName("Edited");
//            user.setSurname("Edited");
//            System.out.println(userRepository.update(user));
//        } catch (Exception ex){
//            System.err.println(ex.getMessage());
//        }

        //Delete
//        try{
//            userRepository.delete(userRepository.findAll().get(0).getId());
//            System.out.println("All done.");
//        } catch (Exception ex){
//            System.err.println(ex.getMessage());
//        }

        //Check function call
//        System.out.println(userRepository.getUserExpensiveCarPrice(100));

        //Find User By Query
//        try{
//            String query = "SELECT *\n" +
//                    "FROM users\n" +
//                    "WHERE AGE(birth_date)>INTERVAL '18 year' AND AGE(birth_date)<= INTERVAL '25 year'";
//            for(User user:userRepository.findUsersByQuery(query)){
//                System.out.println(user);
//            }
//        } catch(Exception ex){
//            System.err.println(ex.getMessage());
//        }

    }
}

