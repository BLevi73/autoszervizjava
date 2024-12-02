package blevi.autoszerviz.controller.logic;

import javax.swing.JOptionPane;

import blevi.autoszerviz.controller.filehandlers.SerializationType;
import blevi.autoszerviz.controller.listeners.*;
import blevi.autoszerviz.controller.wrappers.*;
import blevi.autoszerviz.model.datasources.Data;
import blevi.autoszerviz.model.datatypes.*;
import blevi.autoszerviz.view.dialogs.*;
import blevi.autoszerviz.view.frames.*;

public class MainController {
    private ProgramConfig programConfig;
    private Data data;
    private MainFrame mainFrame;
    private EmployeeData employeeData;
    private ClientData clientData;
    private CarData carData;
    private RepairData repairData;
    private PartData partData;

    public MainController(Data data, ProgramConfig programConfig) {
        this.programConfig = programConfig;
        this.data = data;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public ProgramConfig getProgramConfig() {
        return programConfig;
    }

    private void setupListeners() {
        mainFrame.getMainMenuBar().getFileMenu().getNewMenuItem().addActionListener(new NewDataListener(this));
        mainFrame.getMainMenuBar().getFileMenu().getSaveMenuItem().addActionListener(new SaveDataListener(this));
        mainFrame.getMainMenuBar().getFileMenu().getOpenMenuItem().addActionListener(new OpenDataListener(this));
        mainFrame.getMainMenuBar().getFileMenu().getOpenAutosaveMenuItem()
                .addActionListener(new OpenAutosaveListener(this));
        mainFrame.getMainMenuBar().getFileMenu().getOpenExampleItem().addActionListener(new OpenExampleListener(this));
        mainFrame.getMainMenuBar().getFileMenu().getExportToXMLMenuItem()
                .addActionListener(new XMLExportListener(this));
        mainFrame.getMainMenuBar().getFileMenu().getExportToJSONMenuItem()
                .addActionListener(new JSONExportListener(this));
        mainFrame.getMainMenuBar().getFileMenu().getImportFromJSONMenuItem()
                .addActionListener(new JSONImportListener(this));
        mainFrame.getMainMenuBar().getViewMenu().getToolBarLock().addActionListener(new LockToolbarListener(this));
        mainFrame.getMainMenuBar().getSettingsMenu().getPreferencesItem()
                .addActionListener(new PreferencesListener(this));
        mainFrame.getMainPanel().getMainToolBar().getAddButton().addActionListener(new AddButtonListener(this));
        mainFrame.getMainPanel().getMainToolBar().getRemoveButton().addActionListener(new RemoveButtonListener(this));
        mainFrame.getMainPanel().getMainToolBar().getQueryButton().addActionListener(new QueryButtonListener(this));
    }

    private void setupTables() {
        mainFrame.getMainPanel().getTablesTabbedPane().getEmployeesTable().setModel(employeeData);
        mainFrame.getMainPanel().getTablesTabbedPane().getClientsTable().setModel(clientData);
        mainFrame.getMainPanel().getTablesTabbedPane().getCarsTable().setModel(carData);
        mainFrame.getMainPanel().getTablesTabbedPane().getRepairsTable().setModel(repairData);
        mainFrame.getMainPanel().getTablesTabbedPane().getPartsTable().setModel(partData);
    }

    private void setupTableModels() {
        this.employeeData = new EmployeeData(data.getEmployees());
        this.clientData = new ClientData(data.getClients());
        this.carData = new CarData(data.getCars());
    }

    public void init() {
        this.mainFrame = new MainFrame();
        this.setupListeners();
        this.setupTableModels();
        this.setupTables();
    }

    public void newData() {
        data = new Data();
        mainFrame.getMainPanel().getMainInfoBar().getCurrentFileLabel().setText("-");
        mainFrame.getMainPanel().getMainInfoBar().getNumberOfEmployeesLabel().setText("0");
        this.setupTableModels();
        this.setupTables();
    }

    public void saveData(String filepath, SerializationType type) {
        data.write(filepath, type);
        mainFrame.getMainPanel().getMainInfoBar().getCurrentFileLabel().setText(filepath);
    }

    public void loadData(String filepath, SerializationType type) {
        data.load(filepath, type);
        mainFrame.getMainPanel().getMainInfoBar().getCurrentFileLabel().setText(filepath);
        mainFrame.getMainPanel().getMainInfoBar().getNumberOfEmployeesLabel()
                .setText(Integer.toString(data.getEmployees().size()));
        this.setupTableModels();
        this.setupTables();
    }

    public void openPreferences() {
        int returnVal = PreferencesDialog.showPreferencesDialog();
        if (returnVal == JOptionPane.YES_OPTION) {
            if (PreferencesDialog.getIntervalTextField().getText().isBlank()) {
                programConfig.setAutosaveInterval(PreferencesDialog.getSelectedInterval());
            } else {
                int customInterval = Integer.parseInt(PreferencesDialog.getIntervalTextField().getText());
                if (customInterval > 0) {
                    programConfig.setAutosaveInterval(customInterval);
                } else {
                    JOptionPane.showMessageDialog(null, "Custom interval must be larger than 0!", "Wrong input",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            programConfig.write();
        }
    }

    public void addEmployee() {
        int returnVal = EmployeeDialog.showAddEmployeeDialog();
        if (returnVal == JOptionPane.YES_OPTION) {
            Employee toAdd = new Employee(EmployeeDialog.getIdNumberInputField().getText(),
                    EmployeeDialog.getNameInputField().getText(),
                    EmployeeDialog.getPhoneNumberInputField().getText(),
                    EmployeeDialog.getEmailInputField().getText(),
                    EmployeeDialog.getPositionInputField().getText());
            boolean operationSuccessful = true;
            if (!data.addEmployee(toAdd)) {
                operationSuccessful = false;
            }
            if (!operationSuccessful) {
                JOptionPane.showMessageDialog(null, "This employee is already in the database!", "Operation failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        employeeData.fireTableDataChanged();
        mainFrame.getMainPanel().getMainInfoBar().getNumberOfEmployeesLabel()
                .setText(Integer.toString(data.getEmployees().size()));
    }

    public void addClient() {
        int returnVal = ClientDialog.showAddClientDialog();
        if (returnVal == JOptionPane.YES_OPTION) {
            Client toAdd = new Client(ClientDialog.getIdNumberInputField().getText(),
                    ClientDialog.getNameInputField().getText(),
                    ClientDialog.getPhoneNumberInputField().getText(),
                    ClientDialog.getEmailInputField().getText());
            boolean operationSuccessful = true;
            synchronized (data) {
                if (!clientData.addClient(toAdd)) {
                    operationSuccessful = false;
                }
            }
            if (!operationSuccessful) {
                JOptionPane.showMessageDialog(null, "This client is already in the database!", "Operation failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public void removeEmployee() {
        int selectedRow = mainFrame.getMainPanel().getTablesTabbedPane().getEmployeesTable().getSelectedRow();
        String idNumber = (String) mainFrame.getMainPanel().getTablesTabbedPane().getEmployeesTable()
                .getValueAt(selectedRow, 0);
        String name = (String) mainFrame.getMainPanel().getTablesTabbedPane().getEmployeesTable()
                .getValueAt(selectedRow, 1);
        String phoneNumber = (String) mainFrame.getMainPanel().getTablesTabbedPane().getEmployeesTable()
                .getValueAt(selectedRow, 2);
        String email = (String) mainFrame.getMainPanel().getTablesTabbedPane().getEmployeesTable()
                .getValueAt(selectedRow, 3);
        String position = (String) mainFrame.getMainPanel().getTablesTabbedPane().getEmployeesTable()
                .getValueAt(selectedRow, 4);
        Employee toRemove = new Employee(idNumber, name, phoneNumber, email, position);
        employeeData.removeEmployee(toRemove);
    }

    public int getOpenedTab() {
        return mainFrame.getMainPanel().getTablesTabbedPane().getSelectedIndex();
    }

    public void lockToolbar() {
        mainFrame.getMainPanel().getMainToolBar()
                .setFloatable(!mainFrame.getMainPanel().getMainToolBar().isFloatable());
    }

    public void createEmployeeQuery() {
        int returnVal = EmployeeQueryDialog.showEmployeeQueryDialog();
        Employee filter;
        EmployeeData filteredData;
        if (returnVal == JOptionPane.YES_OPTION) {
            filter = new Employee(EmployeeQueryDialog.getIdNumberInputField().getText(),
                    EmployeeQueryDialog.getNameInputField().getText(),
                    EmployeeQueryDialog.getPhoneNumberInputField().getText(),
                    EmployeeQueryDialog.getEmailInputField().getText(),
                    EmployeeQueryDialog.getPositionInputField().getText());
            filteredData = employeeData.getFilteredData(filter);
            QueryFrame queryFrame = new QueryFrame();
            queryFrame.getTable().setModel(filteredData);
            queryFrame.getFoundRecords().setText("Found records: " + Integer.toString(filteredData.getRowCount()));
        }
    }

    public void createClientQuery() {
        int returnVal = ClientQueryDialog.showClientQueryDialog();
        Client filter;
        ClientData filteredData;
        if (returnVal == JOptionPane.YES_OPTION) {
            filter = new Client(ClientQueryDialog.getIdNumberInputField().getText(),
                    ClientQueryDialog.getNameInputField().getText(),
                    ClientQueryDialog.getPhoneNumberInputField().getText(),
                    ClientQueryDialog.getEmailInputField().getText());
            filteredData = new ClientData(clientData.getFilteredData(filter));
            QueryFrame queryFrame = new QueryFrame();
            queryFrame.getTable().setModel(filteredData);
        }
    }
}