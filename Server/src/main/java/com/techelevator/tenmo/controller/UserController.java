package com.techelevator.tenmo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;

@RestController
public class UserController {

	private UserDAO userDao;

	public UserController(UserDAO userDao) {
		this.userDao = userDao;
	}

	@RequestMapping(path = "/users", method = RequestMethod.GET)
	public List<User> getAllUsers() {
		return userDao.findAll();
	}

	@RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
	public User getUserByUserId(@PathVariable int id) {
		return userDao.findByUserId(id);
	}

}
