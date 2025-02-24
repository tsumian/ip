package duke;

import duke.Exceptions.EmptyMessageException;
import duke.Exceptions.MissingEnquiryDateException;
import duke.Exceptions.WrongDateFormatException;

import java.time.LocalDate;

public class Commands {

    private final ListStorage myStorage;
    private final Printer myPrinter;
    private final UiPrinter myUiPrinter = new UiPrinter();
    private final Statistics myStatistics;

    /**
     * Constructor for Commands. Commands contain a ListStorage
     * and Printer class.
     *
     * @param listStorage Specifies a ListStorage to store commands
     * @param printer Printer to print commands.
     */
    public Commands(ListStorage listStorage, Printer printer) {
        this.myStorage = listStorage;
        this.myPrinter = printer;
        this.myStatistics = new Statistics(this.myStorage);
    }

    /**
     * Prints goodbye message.
     */
    public String cmdBye() {
        myPrinter.printBye();
        return myUiPrinter.printBye();
    }

    /**
     * Parses ToDo command. Parses command and description.
     *
     * @param cmd Command to be parsed.
     * @throws EmptyMessageException Throws exception when there is no description.
     */
    public String cmdTodo(String cmd) throws EmptyMessageException {
        String[] parsedCmd = Parser.parseCmdAndDes(cmd);
        if (cmd.length() == 4) {
            throw new EmptyMessageException("Todo Error");
        }
        Task newTask = new ToDo(parsedCmd[1]);
        myStorage.addToList(newTask);
        assert myStorage.length() != 0 : "Task was not added successfully";
        myPrinter.printTask(newTask, myStorage.length());
        return myUiPrinter.printTask(newTask, myStorage.length());
    }

    /**
     * Parses Deadline command. Parses description and deadline.
     *
     * @param cmd Command to be parsed.
     * @throws EmptyMessageException Thrown when there is no description.
     * @throws WrongDateFormatException Thrown when date format is wrong.
     */
    public String cmdDeadline(String cmd) throws EmptyMessageException, WrongDateFormatException {
        if (cmd.length() == 8) {
            throw new EmptyMessageException("Deadline Error");
        }
        String[] parsedCmd = Parser.parseCmdAndDes(cmd);
        String[] deadline = Parser.splitDeadlineAndTime(parsedCmd[1]);
        String[] checkException = deadline[1].split("-", 3);
        if ((deadline[1].length() != 10 && deadline[1].length() < 17) || deadline[1].length() > 18) {
            //not of correct length
            throw new WrongDateFormatException("Wrong date format");
        } else if (checkException[0].length() != 4) {
            //year was not put first
            throw new WrongDateFormatException("Wrong date format");
        } else if (deadline[1].length() != 10 && !deadline[1].contains(":")) {
            throw new WrongDateFormatException("Wrong time format");
        } else if (deadline[1].length() != 10 && !deadline[1].contains("AM") && !deadline[1].contains("PM")) {
            throw new WrongDateFormatException("Wrong time format");
        }
        //think about how to handle missing deadline exceptions
        Task newTask = new Deadline(deadline[0], deadline[1]);
        myStorage.addToList(newTask);
        assert myStorage.length() != 0 : "Task was not added successfully";
        myPrinter.printTask(newTask, myStorage.length());
        return myUiPrinter.printTask(newTask, myStorage.length());
    }

    /**
     * Parses Event command. Parses description and event timing.
     *
     * @param cmd Command to be parsed
     * @throws EmptyMessageException Thrown when there is no description.
     * @throws WrongDateFormatException Thwon when date format is wrong.
     */
    public String cmdEvent(String cmd) throws EmptyMessageException, WrongDateFormatException {
        if (cmd.length() == 5) {
            throw new EmptyMessageException("Event Error");
        }
        String[] parsedCmd = Parser.parseCmdAndDes(cmd);
        String[] event = Parser.splitEventAndTime(parsedCmd[1]);
        String[] checkException = event[1].split("-", 3);
        //think about how to handle missing at exceptions
        if ((event[1].length() != 10 && event[1].length() < 17) || event[1].length() > 18) {
            //not of correct length
            throw new WrongDateFormatException("Wrong date format");
        } else if (checkException[0].length() != 4) {
            //year was not put first
            throw new WrongDateFormatException("Wrong date format");
        } else if (event[1].length() != 10 && !event[1].contains(":")) {
            throw new WrongDateFormatException("Wrong time format");
        } else if (event[1].length() != 10 && !event[1].contains("AM") && !event[1].contains("PM")) {
            throw new WrongDateFormatException("Wrong time format");
        }
        Task newTask = new Event(event[0], event[1]);
        myStorage.addToList(newTask);
        assert myStorage.length() != 0 : "Task was not added successfully";
        myPrinter.printTask(newTask, myStorage.length());
        return myUiPrinter.printTask(newTask, myStorage.length());
    }

    /**
     * Parses list command. Handles empty lists and
     * lists with multiple tasks.
     */
    public String cmdList() {
        if (myStorage.length() == 0) {
            myPrinter.printEmptyList();
            return myUiPrinter.printEmptyList();
        } else {
            myPrinter.printList(myStorage);
            return myUiPrinter.printList(myStorage);
        }
    }

    /**
     * Handles Unmark command.
     *
     * @param taskNumber Task number of task to be unmarked.
     */
    public String cmdUnmark(int taskNumber) {
        myStorage.findTask(taskNumber).unmark();
        myPrinter.printUnmark(myStorage, taskNumber);
        return myUiPrinter.printUnmark(myStorage, taskNumber);
    }

    /**
     * Handles mark command.
     *
     * @param taskNumber Task number of task to be marked.
     */
    public String cmdMark(int taskNumber) {
        myStorage.findTask(taskNumber).markAsDone();
        myPrinter.printMark(myStorage, taskNumber);
        return myUiPrinter.printMark(myStorage, taskNumber);
    }

    /**
     * Handles delete command.
     *
     * @param taskNumber Task number of task to be deleted.
     */
    public String cmdDelete(int taskNumber) {
        myPrinter.printDelete(myStorage, taskNumber);
        String result = myUiPrinter.printDelete(myStorage, taskNumber);
        myStorage.deleteTask(taskNumber);
        return result;
    }

    /**
     * Handles find command. Prints our a list of tasks
     * with keyword in description.
     *
     * @param cmd Command to find.
     */
    public String cmdFind(String cmd) {
        String[] keyword = Parser.parseCmdAndDes(cmd);
        ListStorage tempStorage = new ListStorage();
        for (int i = 1; i <= myStorage.length(); i++) {
            if (myStorage.findTask(i).description.contains(keyword[1])) {
                tempStorage.addToList(myStorage.findTask(i));
            }
        }
        myPrinter.printList(tempStorage);
        return myUiPrinter.printList(tempStorage);
    }

    /**
     * Returns a list of completed tasks during the period of enquiry.
     * @param cmd Command containing keyword and period of enquiry.
     * @return List of completed tasks.
     */
    public String cmdStats(String cmd) throws MissingEnquiryDateException {
        String[] cmds = Parser.splitSearchAndDays(cmd);
        LocalDate day;
        if (cmds.length <= 1) {
            throw new MissingEnquiryDateException("Missing enquiry date");
        }
        String toFind = cmds[1];
        String result;
        switch (toFind) {
        case "today":
            day = LocalDate.now();
            result = myStatistics.getCompletedTask(day);
            System.out.println(result);
            return result;
        case "this week":
            day = LocalDate.now().minusDays(7);
            result = myStatistics.getCompletedTask(day);
            System.out.println(result);
            return result;
        case "this month":
            day = LocalDate.now().minusDays(31);
            result = myStatistics.getCompletedTask(day);
            System.out.println(result);
            return result;
        default:
            long convertDaysToInt = Long.parseLong(toFind);
            day = LocalDate.now().minusDays(convertDaysToInt);
            result = myStatistics.getCompletedTask(day);
            System.out.println(result);
            return result;
        }
    }
}
