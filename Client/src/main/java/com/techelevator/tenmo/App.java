package com.techelevator.tenmo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.TransferStatuses;
import com.techelevator.tenmo.models.TransferType;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN,
			MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS,
			MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS,
			MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	private Scanner scanner = new Scanner(System.in);
	private RestTemplate apiCall = new RestTemplate();

	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		printWelcomeHeader();
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			mainMenuHeader();
			System.out.println();

			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance(currentUser);
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance(AuthenticatedUser currentUser) {
		Account usersAccount = getUserAccount(currentUser.getUser().getId());
		String formatDouble = String.format("%.2f", usersAccount.getBalance());
		System.out.println("-------------------------------------------------");
		System.out.println("Your current account balance is: $" + formatDouble);
		System.out.println("-------------------------------------------------");
		enterToContinue();
	}

	//Get the account of the user by their ID
	private Account getUserAccount(int currentUserId) {
		ResponseEntity<Account> responseEntity = apiCall
				.getForEntity(API_BASE_URL + "/accounts/searchUserId?userId=" + currentUserId, Account.class);
		return responseEntity.getBody();
	}

	//Get user info based on UserID
	private User getUserByUserId(int userId) {
		ResponseEntity<User> responseEntity = apiCall.getForEntity(API_BASE_URL + "/users/" + userId, User.class);
		return responseEntity.getBody();
	}

	private void viewTransferHistory() {
		//Get the singed in users Account
		Account userAccount = getUserAccount(currentUser.getUser().getId());

		List<Transfer> usersTransfers = new ArrayList<>();
		
		
		ResponseEntity<Transfer[]> responseEntity = apiCall
				.getForEntity(API_BASE_URL + "/transfers/byAccount?account_from=" + userAccount.getAccountId()
						+ "&account_to=" + userAccount.getAccountId(), Transfer[].class);
		usersTransfers = Arrays.asList(responseEntity.getBody());
		
		//Sort the Transfers by ID
		usersTransfers = sortTransferList(usersTransfers);
	 

		if (usersTransfers.size() == 0) {
			System.out.println("You currently have no Transfer History");
			enterToContinue();
			return;
		}

		System.out.println("-------------------------------------------------");
		System.out.println("Transfers");
		System.out.println("ID       From/To                          Amount");
		System.out.println("-------------------------------------------------");

		for (Transfer transfer : usersTransfers) {
			Account otherAccount = new Account();
			User otherUser = new User();

			String formatAmount = String.format("%.2f", transfer.getAmount());

			// Base amount of spaces if TransferId is less than 10
			int spacesToAddAfterId = 8;

			// If TransferId is 10 or greater take out 1 space to keep names lined up
			if (transfer.getTransferId() >= 10) {
				spacesToAddAfterId = 7;
			}

			int spacesToAddAfterName = 0;

			// Transfers FROM the account
			if (transfer.getAccountFromId() == userAccount.getAccountId()) {
				//Get the other users account and User info that singed in user Interacted with
				otherAccount = getUserAccount(transfer.getAccountToId());
				otherUser = getUserByUserId(otherAccount.getUserId());

				// Get userName length to determine spaces needed to add
				spacesToAddAfterName = 28 - otherUser.getUsername().length();

				System.out.print(transfer.getTransferId());

				// Adds spaces after TransferId to keep names lined up
				for (int i = 0; i < spacesToAddAfterId; i++) {
					System.out.print(" ");
				}

				System.out.print("To:   " + otherUser.getUsername());

				// Adds spaces after name to keep amounts lined up
				for (int i = 0; i < spacesToAddAfterName; i++) {
					System.out.print(" ");
				}

				System.out.print("$" + formatAmount);
				System.out.println();

			}
			// Transfers to another account
			else if (transfer.getAccountToId() == userAccount.getAccountId()) {
				//Get the other users account and User info that singed in user Interacted with
				otherAccount = getUserAccount(transfer.getAccountFromId());
				otherUser = getUserByUserId(otherAccount.getUserId());

				// Get userName length to determine spaces needed to add
				spacesToAddAfterName = 26 - otherUser.getUsername().length();

				System.out.print(transfer.getTransferId());

				// Adds spaces after TransferId to keep names lined up
				for (int i = 0; i < spacesToAddAfterId; i++) {
					System.out.print(" ");
				}

				System.out.print("From: " + otherUser.getUsername());

				// Adds spaces after name to keep amounts lined up
				for (int i = 0; i < spacesToAddAfterName; i++) {
					System.out.print(" ");
				}

				System.out.print("  $" + formatAmount);
				System.out.println();
			}
		}

		System.out.println("-------------------------------------------------");
		System.out.println("Please enter transfer ID to view details (0 to cancel):");
		String userInput = scanner.nextLine();

		int transferId = 0;
		try {
			transferId = Integer.parseInt(userInput);
		} catch (NumberFormatException ex) {
			System.out.println("Please enter a valid number value");
			enterToContinue();
			return;
		}

		if (transferId == 0) {
			return;
		}

		for (Transfer transfer : usersTransfers) {
			if (transfer.getTransferId() == transferId) {
				viewSingleTransfer(transfer);
				return;
			}
		}
		System.out.println("Sorry, could not find a transfer with ID of " + transferId);
		enterToContinue();
	}

	private void viewSingleTransfer(Transfer transfer) {
		//Get the other users account and User info that singed in user Interacted with
		Account otherAccount = new Account();
		User otherUser = new User();

		System.out.println("--------------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("--------------------------------------------");

		System.out.println("Id: " + transfer.getTransferId());

		if (transfer.getAccountFromId() == currentUser.getUser().getId()) {
			otherAccount = getUserAccount(transfer.getAccountToId());
			otherUser = getUserByUserId(otherAccount.getAccountId());

			System.out.println("From: " + currentUser.getUser().getUsername());
			System.out.println("To: " + otherUser.getUsername());
		} else {
			otherAccount = getUserAccount(transfer.getAccountFromId());
			otherUser = getUserByUserId(otherAccount.getAccountId());
			System.out.println("From: " + otherUser.getUsername());
			System.out.println("To: " + currentUser.getUser().getUsername());
		}

		ResponseEntity<TransferType> responseTypeEntity = apiCall
				.getForEntity(API_BASE_URL + "/transferType/" + transfer.getTransferTypeId(), TransferType.class);
		TransferType type = responseTypeEntity.getBody();

		System.out.println("Type: " + type.getTransferTypeDescription());

		ResponseEntity<TransferStatuses> responseStatusesEntity = apiCall.getForEntity(
				API_BASE_URL + "/transferStatus/" + transfer.getTransferStatusId(), TransferStatuses.class);
		TransferStatuses status = responseStatusesEntity.getBody();

		System.out.println("Status: " + status.getTransferStatusDescription());

		String amountFormat = String.format("%.2f", transfer.getAmount());
		System.out.println("Amount: $" + amountFormat);
		System.out.println("--------------------------------------------");
		System.out.println();
		enterToContinue();
	}

	private void viewPendingRequests() {
		//Get Account info for singed in User
		Account userAccount = getUserAccount(currentUser.getUser().getId());

		List<Transfer> usersTransfers = new ArrayList<>();
		ResponseEntity<Transfer[]> responseEntity = apiCall
				.getForEntity(API_BASE_URL + "/transfers/byAccount?account_from=" + userAccount.getAccountId()
						+ "&account_to=" + userAccount.getAccountId(), Transfer[].class);
		usersTransfers = Arrays.asList(responseEntity.getBody());
		
		//Sort the transfers ArrayList
		usersTransfers = sortTransferList(usersTransfers);
		
		boolean hasRequest = false;

		for (Transfer transfer : usersTransfers) {
			if (transfer.getAccountFromId() == userAccount.getAccountId() && transfer.getTransferStatusId() == 1) {
				hasRequest = true;
			}
		}

		if (!hasRequest) {
			System.out.println("You currently have no pending Request!");
			enterToContinue();
			return;
		}

		System.out.println("---------------------------------------------------");
		System.out.println("Transfers");
		System.out.println("ID              To                          Amount");
		System.out.println("---------------------------------------------------");

		List<Transfer> pendingRequests = new ArrayList<>();

		for (Transfer transfer : usersTransfers) {
			Account otherAccount = new Account();
			User otherUser = new User();

			String formatAmount = String.format("%.2f", transfer.getAmount());

			if (transfer.getAccountFromId() == userAccount.getAccountId() && transfer.getTransferStatusId() == 1) {
				pendingRequests.add(transfer);
				
				//Get the other users account and User info that singed in user Interacted with
				otherAccount = getUserAccount(transfer.getAccountToId());
				otherUser = getUserByUserId(otherAccount.getUserId());

				// Base amount of spaces if TransferId is less than 10
				int spacesToAddAfterId = 8;

				// If TransferId is 10 or greater take out 1 space to keep names lined up
				if (transfer.getTransferId() >= 10) {
					spacesToAddAfterId = 7;
				}

				// Get length of user name to determine how many spaces to add
				int spacesToAdd = 28 - otherUser.getUsername().length();

				System.out.print(transfer.getTransferId());

				// Adds spaces after TransferId to keep names lined up
				for (int i = 0; i < spacesToAddAfterId; i++) {
					System.out.print(" ");
				}

				System.out.print("To: " + otherUser.getUsername());

				// Add spaces after userName to keep amount lined up
				for (int i = 0; i < spacesToAdd; i++) {
					System.out.print(" ");
				}
				System.out.print("$" + formatAmount);
				System.out.println();
			}
		}
		
		//Sort the pending request List
		pendingRequests = sortTransferList(pendingRequests);

		System.out.println("Please enter transfer ID to approve/reject (0 to cancel)");

		String userChoice = scanner.nextLine();

		int transferId = 0;

		try {
			transferId = Integer.parseInt(userChoice);
		} catch (NumberFormatException ex) {
			System.out.println("Please enter a valid transfer ID");
			enterToContinue();
			return;
		}
		if (transferId == 0) {

			return;
		}

		for (Transfer transfer : pendingRequests) {
			if (transferId == transfer.getTransferId()) {

				System.out.println("1: Approve");
				System.out.println("2: Reject");
				System.out.println("0: Don't Approve or Reject");
				System.out.println("-----------------------------");
				System.out.print("Please Choose an option: ");

				String option = scanner.nextLine();

				switch (option) {

				case "1":
					approveTransfer(transfer);
					break;

				case "2":
					rejectTransfer(transfer);
					break;
				}
				
				return;
			}
		}
		
		System.out.println("Sorry, could not find a transfer with ID of " + transferId);
		enterToContinue();
		
	}

	private void approveTransfer(Transfer transfer) {
		Account fromAccount = getUserAccount(transfer.getAccountFromId());
		Account toAccount = getUserAccount(transfer.getAccountToId());

		if (fromAccount.getBalance() >= transfer.getAmount()) {

			fromAccount.setBalance(fromAccount.getBalance() - transfer.getAmount());
			toAccount.setBalance(toAccount.getBalance() + transfer.getAmount());

			apiCall.put(API_BASE_URL + "/accounts/" + fromAccount.getAccountId(), fromAccount);
			apiCall.put(API_BASE_URL + "/accounts/" + toAccount.getAccountId(), toAccount);

			transfer.setTransferStatusId(2);
			apiCall.put(API_BASE_URL + "/transfers/" + transfer.getTransferId(), transfer);

			System.out.println("Your request has been approved!");
			enterToContinue();

		} else {
			System.out.println("I'm sorry but you don't have enough funds to approve this request");
			enterToContinue();
		}
	}

	private void rejectTransfer(Transfer transfer) {
		transfer.setTransferStatusId(3);
		apiCall.put(API_BASE_URL + "/transfers/" + transfer.getTransferId(), transfer);

		System.out.println("You have successfully rejected the request :)");
		enterToContinue();
	}

	private void sendBucks() {
		//Get account info for singed in User
		Account usersAccount = getUserAccount(currentUser.getUser().getId());
		
		//Get all other user(will not include singed in User)
		List<User> userList = printUsers(currentUser.getUser().getId());
		System.out.print("Enter ID of user you are sending to (0 to cancel): ");
		String userChoice = scanner.nextLine();

		int userId = 0;
		try {
			userId = Integer.parseInt(userChoice);
		} catch (NumberFormatException ex) {
			System.out.println("Enter a Valid id");
			enterToContinue();
			return;
		}
		if (userId == 0) {
			return;
		}

		if (userId == usersAccount.getAccountId()) {
			System.out.println("You cannot send money to your own account!");
			enterToContinue();
			return;
		}

		for (User user : userList) {
			if (user.getId() == userId) {
				System.out.print("Enter amount: ");
				String amountInput = scanner.nextLine();
				double amountToTransfer = 0;

				try {
					amountToTransfer = Double.parseDouble(amountInput);
					String formatTransferAmount = String.format("%.2f", amountToTransfer);
					amountToTransfer = Double.parseDouble(formatTransferAmount);
				} catch (NumberFormatException ex) {
					System.out.println("Please enter a valid dollar amount");
					enterToContinue();
					return;
				}

				if (amountToTransfer > usersAccount.getBalance()) {
					System.out.println("Sorry you cannot transfer more money than you have");
					enterToContinue();
					return;
				} else if (amountToTransfer <= 0) {
					System.out.println("Sorry you cannot transfer 0 or negative dollars");
					enterToContinue();
					return;
				}

				Account transferToAccount = getUserAccount(userId);
				transferToAccount.setBalance(transferToAccount.getBalance() + amountToTransfer);

				usersAccount.setBalance(usersAccount.getBalance() - amountToTransfer);

				apiCall.put(API_BASE_URL + "/accounts/" + usersAccount.getAccountId(), usersAccount);
				apiCall.put(API_BASE_URL + "/accounts/" + transferToAccount.getAccountId(), transferToAccount);

				Transfer transfer = new Transfer();
				transfer.setTransferTypeId(apiCall.getForEntity(API_BASE_URL + "/transferType/2", TransferType.class)
						.getBody().getTransferTypeId());
				transfer.setTransferStatusId(
						apiCall.getForEntity(API_BASE_URL + "/transferStatus/2", TransferStatuses.class).getBody()
								.getTransferStatusId());
				transfer.setAccountFromId(usersAccount.getAccountId());
				transfer.setAccountToId(transferToAccount.getAccountId());
				transfer.setAmount(amountToTransfer);

				apiCall.postForEntity(API_BASE_URL + "/transfers", transfer, Transfer.class);
				
				System.out.println("-------------------------------------------------");
				System.out.println("Transfer Sent Succesfully!");
				System.out.println("-------------------------------------------------");
				enterToContinue();
				
				return;
			}
		}
		
		System.out.println("Sorry no User with Id \"" + userId + "\" was found.");
		enterToContinue();
		
	}

	private void requestBucks() {
		//Get account info for singed in User
		Account usersAccount = getUserAccount(currentUser.getUser().getId());
		
		//Get all other user(will not include singed in User)
		List<User> userList = printUsers(currentUser.getUser().getId());
		System.out.print("Enter ID of user you are requesting from (0 to cancel): ");
		String userChoice = scanner.nextLine();

		int userId = 0;
		try {
			userId = Integer.parseInt(userChoice);
		} catch (NumberFormatException ex) {
			System.out.println("Enter a Valid id");
			enterToContinue();
			return;
		}
		if (userId == 0) {
			return;
		}

		if (userId == usersAccount.getAccountId()) {
			System.out.println("You cannot request money from your own account!");
			enterToContinue();
			return;
		}

		for (User user : userList) {
			if (user.getId() == userId) {
				System.out.print("Enter amount: ");
				String amountInput = scanner.nextLine();
				double amountToRequest = 0;

				try {
					amountToRequest = Double.parseDouble(amountInput);
					String formatRequestAmount = String.format("%.2f", amountToRequest);
					amountToRequest = Double.parseDouble(formatRequestAmount);
				} catch (NumberFormatException ex) {
					System.out.println("Please enter a valid dollar amount");
					enterToContinue();
					return;
				}

				if (amountToRequest <= 0) {
					System.out.println("You cannot request 0 dollars");
					enterToContinue();
					return;
				}

				Account requesteeAccount = getUserAccount(userId);
				Transfer requestTransfer = new Transfer();

				requestTransfer
						.setTransferTypeId(apiCall.getForEntity(API_BASE_URL + "/transferType/1", TransferType.class)
								.getBody().getTransferTypeId());
				requestTransfer.setTransferStatusId(
						apiCall.getForEntity(API_BASE_URL + "/transferStatus/1", TransferStatuses.class).getBody()
								.getTransferStatusId());
				requestTransfer.setAccountFromId(requesteeAccount.getAccountId());
				requestTransfer.setAccountToId(usersAccount.getAccountId());
				requestTransfer.setAmount(amountToRequest);
				apiCall.postForEntity(API_BASE_URL + "/transfers", requestTransfer, Transfer.class);

				System.out.println("-------------------------------------------------");
				System.out.println("Request Sent Succesfully!");
				System.out.println("-------------------------------------------------");
				enterToContinue();
				return;
			}
		}
		
		System.out.println("Sorry no User with Id \"" + userId + "\" was found.");
		enterToContinue();
	}

	private void exitProgram() {
		printExitMessage();
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		printRegisterHeader();
		System.out.println();
		System.out.println("Please register a new user account(Or press enter to go back to the main menu): ");
		boolean isRegistered = false;
		while (!isRegistered) // will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();

			if (credentials.getUsername().equalsIgnoreCase("") || credentials.getUsername().equalsIgnoreCase(null)) {
				return;
			}

			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		printLoginHeader();
		System.out.println();
		currentUser = null;
		while (currentUser == null) // will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private List<User> printUsers(int userId) {
		ResponseEntity<User[]> responseEntity = apiCall.getForEntity(API_BASE_URL + "/users", User[].class);

		List<User> userList = Arrays.asList(responseEntity.getBody());

		for (User user : userList) {
			if (user.getId() != userId) {
				System.out.println(user.getId() + ": " + user.getUsername());
			}
		}
		return userList;
	}
	
	private List<Transfer> sortTransferList(List<Transfer> transfers){
		Collections.sort(transfers, new Comparator<Transfer>() {
            @Override
            public int compare(Transfer t1, Transfer t2) {
                return t1.getTransferId() - t2.getTransferId();
            }
        });
		
		return transfers;
	}

	private void enterToContinue() {
		System.out.println("Press ENTER to continue.");
		scanner.nextLine();
	}

	private void printWelcomeHeader() {
		System.out.println("  _      __    __                     __         ______         __  ___   ");
		System.out.println(" | | /| / /__ / /______  __ _  ___   / /____    /_  __/__ ___  /  |/  /__ ");
		System.out.println(" | |/ |/ / -_) / __/ _ \\/  ' \\/ -_) / __/ _ \\    / / / -_) _ \\/ /|_/ / _ \\");
		System.out.println(" |__/|__/\\__/_/\\__/\\___/_/_/_/\\__/  \\__/\\___/   /_/  \\__/_//_/_/  /_/\\___/");
		System.out.println("                                                                          ");
	}

	private void printLoginHeader() {
		System.out.println("   __             _    ");
		System.out.println("  / /  ___  ___ _(_)__ ");
		System.out.println(" / /__/ _ \\/ _ `/ / _ \\");
		System.out.println("/____/\\___/\\_, /_/_//_/");
		System.out.println("          /___/        ");

	}

	private void printRegisterHeader() {
		System.out.println("   ___           _     __         ");
		System.out.println("  / _ \\___ ___ _(_)__ / /____ ____");
		System.out.println(" / , _/ -_) _ `/ (_-</ __/ -_) __/");
		System.out.println("/_/|_|\\__/\\_, /_/___/\\__/\\__/_/   ");
		System.out.println("         /___/                    ");

	}

	private void mainMenuHeader() {
		System.out.println(" ______         __  ___   ");
		System.out.println("/_  __/__ ___  /  |/  /__ ");
		System.out.println(" / / / -_) _ \\/ /|_/ / _ \\");
		System.out.println("/_/  \\__/_//_/_/  /_/\\___/");
		System.out.println("                          ");
	}

	private void printExitMessage() {
		System.out.println("Thanks for banking with....");
		mainMenuHeader();
	}
}
