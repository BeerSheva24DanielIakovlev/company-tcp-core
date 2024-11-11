package telran.employees;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import telran.net.TcpClient;

public class CompanyTcpProxy implements Company {
    TcpClient tcpClient;

    public CompanyTcpProxy(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void addEmployee(Employee empl) {
        JSONObject json = new JSONObject();
        json.put("id", empl.getId());
        json.put("basicSalary", empl.getBasicSalary());
        json.put("department", empl.getDepartment());
    
        if (empl instanceof Manager) {
            json.put("type", "Manager");
            json.put("factor", ((Manager) empl).getFactor());
        } else if (empl instanceof SalesPerson) {
            SalesPerson sp = (SalesPerson) empl;
            json.put("type", "SalesPerson");
            json.put("hours", sp.getHours());
            json.put("wage", sp.getWage());
        } else if (empl instanceof WageEmployee) {
            WageEmployee we = (WageEmployee) empl;
            json.put("type", "WageEmployee");
            json.put("hours", we.getHours());
            json.put("wage", we.getWage());
        } else {
            json.put("type", "Employee");
        }
    
        tcpClient.sendAndReceive("addEmployee", json.toString());
    }
    

    @Override
    public Employee getEmployee(long id) {
        String jsonResponse = tcpClient.sendAndReceive("getEmployee", Long.toString(id));
        if (jsonResponse == null) {
            return null;
        }
        return parseEmployee(new JSONObject(jsonResponse));
    }

    @Override
    public int getDepartmentBudget(String department) {
        String jsonResponse = tcpClient.sendAndReceive("getDepartmentBudget", department);
        return Integer.parseInt(jsonResponse);
    }

    @Override
    public String[] getDepartments() {
        String jsonResponse = tcpClient.sendAndReceive("getDepartments", "");
        JSONArray jsonArray = new JSONArray(jsonResponse);
        return jsonArray.toList().toArray(new String[0]);
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        String jsonResponse = tcpClient.sendAndReceive("getManagersWithMostFactor", "");
        JSONArray jsonArray = new JSONArray(jsonResponse);
        Manager[] managers = new Manager[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            managers[i] = (Manager) parseEmployee(jsonArray.getJSONObject(i));
        }
        return managers;
    }

    @Override
    public Employee removeEmployee(long id) {
        String jsonResponse = tcpClient.sendAndReceive("removeEmployee", Long.toString(id));
        if (jsonResponse == null) {
            return null;
        }
        return parseEmployee(new JSONObject(jsonResponse));
    }

    @Override
    public Iterator<Employee> iterator() {
        return null;
    }
    
    private Employee parseEmployee(JSONObject jsonObject) {
        long id = jsonObject.getLong("id");
        int basicSalary = jsonObject.getInt("basicSalary");
        String department = jsonObject.getString("department");

        switch (jsonObject.getString("type")) {
            case "Manager":
                float factor = jsonObject.getFloat("factor");
                return new Manager(id, basicSalary, department, factor);
            case "SalesPerson":
                int hours = jsonObject.getInt("hours");
                int wage = jsonObject.getInt("wage");
                float percents = jsonObject.getFloat("percents");
                long sales = jsonObject.getLong("sales");
                return new SalesPerson(id, basicSalary, department, hours, wage, percents, sales);
            case "WageEmployee":
                hours = jsonObject.getInt("hours");
                wage = jsonObject.getInt("wage");
                return new WageEmployee(id, basicSalary, department, hours, wage);
            default:
                return new Employee(id, basicSalary, department);
        }
    }
}
