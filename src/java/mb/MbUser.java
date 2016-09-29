/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import domain.BettingUser;
import domain.Game;
import domain.Ticket;
import domain.TicketMatch;
import domain.TicketPK;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import sb.BettingUserFacadeLocal;
import sb.GameFacadeLocal;
import sb.StatusFacadeLocal;
import sb.TicketFacadeLocal;
import sb.TicketMatchFacadeLocal;
import util.JsfUtil;

/**
 *
 * @author Ivan
 */
@ManagedBean
@SessionScoped
public class MbUser implements Serializable{

    private BettingUser user;
    private List<Game> notStartedGameList;
    private Game selectedGame;
    private List<Ticket> ticketList;
    private Ticket selectedTicket;
    private List<TicketMatch> newTicketGames;
    private TicketMatch ticketMatchToRemove;
    private String selectedDataTableValue;

    @EJB
    private BettingUserFacadeLocal ejbUser;

    @EJB
    private GameFacadeLocal ejbGame;

    @EJB
    private TicketFacadeLocal ejbTicket;

    @EJB
    private TicketMatchFacadeLocal ejbTicketMatch;

    @EJB
    private StatusFacadeLocal ejbStatus;

    public MbUser() {
    }

    @PostConstruct
    public void init() {
        user = new BettingUser();
        ticketList = new ArrayList<>();
    }

    public String logIn() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        BettingUser u = getEjbUser().logIn(user);
        if (u == null) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("localization.messages", locale).getString("LogInUserMessage_failure"));
            return "index";
        }
        user = u;
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("localization.messages", locale).getString("LogInUserMessage_success"));
        return "user?faces-redirect=true";
    }

    public String logOut() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        user = new BettingUser();
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("localization.messages", locale).getString("UserMessage_successLogOut"));
        return "index?faces-redirect=true";
    }

    public String registerNewUser() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        try {
            getEjbUser().registerNewUser(user);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("localization.messages", locale).getString("RegistrationUser_success"));
        } catch (Exception ex) {
            String msg = ex.getMessage();

            if (msg.equals("AdministratorWithEmailExistException") || msg.equals("UserWithEmailExistException")) {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("localization.messages", locale).getString(msg));
            } else {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("localization.messages", locale).getString("RegistrationUser_persistenceErrorOccured"));
            }
        }
        return "index";
    }

    public void prepareTicketCreation() {
        newTicketGames = new ArrayList<>();
        notStartedGameList = getEjbGame().getGameStartingAfter(new Date());
        selectedTicket = new Ticket(new TicketPK(user.getEmail(), getEjbTicket().getNewTicketId()));
        selectedTicket.setStatus(getEjbStatus().find(3));
        selectedTicket.setBettingUser(user);
        selectedTicket.setTicketMatchList(newTicketGames);
        selectedTicket.setStake(30.0);
    }

    public void saveTicket() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        try {
            selectedTicket.setPaymentTime(new Date());
            getEjbTicket().saveTicket(selectedTicket);
            double newUserBalance = user.getBalance() - selectedTicket.getStake();
            user.setBalance(newUserBalance);
            getEjbUser().edit(user);
            user = getEjbUser().find(user.getEmail());
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("localization.messages", locale).getString("UserCreateTicket_successMessage"));
        } catch (Exception ex) {
            String msg = ex.getMessage();

            if (msg.equals("Exception_TicketAlreadyExist") || msg.equals("Exception_TicketStakeIsLessThanMin") || msg.equals("Exception_NoGameOnTheTicket")) {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("localization.messages", locale).getString(msg));
            } else {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("localization.messages", locale).getString("RegistrationUser_persistenceErrorOccured"));
            }
        }
    }

    public List<Ticket> getUserTickets() {
        ticketList = getEjbTicket().getUserTickets(user);
        return ticketList;
    }

    public BettingUser getUser() {
        return user;
    }

    public void setUser(BettingUser user) {
        this.user = user;
    }

    public BettingUserFacadeLocal getEjbUser() {
        return ejbUser;
    }

    public void setEjbUser(BettingUserFacadeLocal ejbUser) {
        this.ejbUser = ejbUser;
    }

    public GameFacadeLocal getEjbGame() {
        return ejbGame;
    }

    public void setEjbGame(GameFacadeLocal ejbGame) {
        this.ejbGame = ejbGame;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    public Ticket getSelectedTicket() {
        return selectedTicket;
    }

    public void setSelectedTicket(Ticket selectedTicket) {
        this.selectedTicket = selectedTicket;
    }

    public TicketFacadeLocal getEjbTicket() {
        return ejbTicket;
    }

    public void setEjbTicket(TicketFacadeLocal ejbTicket) {
        this.ejbTicket = ejbTicket;
    }

    public Game getSelectedGame() {
        return selectedGame;
    }

    public void onGameSelected() {
        if (selectedDataTableValue == null) {
            return;
        }
        String gameId = selectedDataTableValue.split(":")[0];
        String stringPrediction = selectedDataTableValue.split(":")[1];

        Game game = null;

        for (Game g : notStartedGameList) {
            if (g.getId().equals(gameId)) {
                game = g;
                break;
            }
        }

        if (game == null) {
            return;
        }

        this.selectedGame = game;

        int prediction = -1;
        double odds = 1;
        int selectedValue = Integer.parseInt(stringPrediction);
        switch (selectedValue) {
            case 4:
                prediction = 1;
                odds = selectedGame.getHomeOdds();
                break;
            case 5:
                prediction = 0;
                odds = selectedGame.getDrawOdds();
                break;
            case 6:
                prediction = 2;
                odds = selectedGame.getAwayOdds();
                break;
            default:
                prediction = -1;
                odds = 1;
        }

        if (prediction == -1 || this.selectedTicket == null && this.selectedGame == null) {
            return;
        }

        TicketMatch tm = new TicketMatch(selectedTicket.getTicketPK().getEmailUser(), selectedTicket.getTicketPK().getId(), selectedGame.getId());

        if (newTicketGames.contains(tm)) {
            TicketMatch tm2 = newTicketGames.get(newTicketGames.indexOf(tm));
            tm2.setResultPrediction(prediction);
            tm2.setOdd(odds);
            calculatePotentialWinnings();
            return;
        }
        tm.setTicket(selectedTicket);
        tm.setGame(selectedGame);
        tm.setStatus(getEjbStatus().find(3));
        tm.setOdd(odds);
        tm.setResultPrediction(prediction);
        newTicketGames.add(tm);
        calculatePotentialWinnings();
    }

    public void calculatePotentialWinnings() {
        if (selectedTicket == null) {
            return;
        }

        BigDecimal totalOdds = BigDecimal.ONE;
        for (TicketMatch tm : selectedTicket.getTicketMatchList()) {
            totalOdds = totalOdds.multiply(BigDecimal.valueOf(tm.getOdd()));
        }
        totalOdds.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal possibleWinnings = BigDecimal.ONE;
        possibleWinnings = totalOdds.multiply(BigDecimal.valueOf(selectedTicket.getStake()));
        possibleWinnings.setScale(2, BigDecimal.ROUND_HALF_UP);

        selectedTicket.setTotalOdds(totalOdds.doubleValue());
        selectedTicket.setPotentialWinnings(possibleWinnings.doubleValue());
    }

    public TicketMatchFacadeLocal getEjbTicketMatch() {
        return ejbTicketMatch;
    }

    public void setEjbTicketMatch(TicketMatchFacadeLocal ejbTicketMatch) {
        this.ejbTicketMatch = ejbTicketMatch;
    }

    public StatusFacadeLocal getEjbStatus() {
        return ejbStatus;
    }

    public void setEjbStatus(StatusFacadeLocal ejbStatus) {
        this.ejbStatus = ejbStatus;
    }

    public List<Game> getNotStartedGameList() {
        return notStartedGameList;
    }

    public void setNotStartedGameList(List<Game> notStartedGameList) {
        this.notStartedGameList = notStartedGameList;
    }

    public List<TicketMatch> getNewTicketGames() {
        return newTicketGames;
    }

    public void setNewTicketGames(List<TicketMatch> newTicketGames) {
        this.newTicketGames = newTicketGames;
    }

    public TicketMatch getTicketMatchToRemove() {
        return ticketMatchToRemove;
    }

    public void setTicketMatchToRemove(TicketMatch ticketMatchToRemove) {
        this.ticketMatchToRemove = ticketMatchToRemove;
        if (newTicketGames.contains(ticketMatchToRemove)) {
            newTicketGames.remove(ticketMatchToRemove);
            calculatePotentialWinnings();
        }
    }

    public String getSelectedDataTableValue() {
        return selectedDataTableValue;
    }

    public void setSelectedDataTableValue(String selectedDataTableValue) {
        this.selectedDataTableValue = selectedDataTableValue;
    }

}
