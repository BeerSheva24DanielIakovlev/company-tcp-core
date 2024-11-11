package telran.employees;

import org.json.JSONObject;
import telran.net.*;
import static telran.net.ResponseCode.*;
import java.util.Arrays;

public class CompanyProtocol implements Protocol {
    private Company company;

    public CompanyProtocol(Company company) {
        this.company = company;
    }

    @Override
    public Response getResponse(Request request) {
        String requestType = request.requestType();
        String requestData = request.requestData();

        try {
            switch (requestType) {
                case "addEmployee":
                    return addEmployee(requestData);
                case "getEmployee":
                    return getEmployee(requestData);
                case "removeEmployee":
                    return removeEmployee(requestData);
                case "getDepartmentBudget":
                    return getDepartmentBudget(requestData);
                case "getDepartments":
                    return getDepartments();
                case "getManagersWithMostFactor":
                    return getManagersWithMostFactor();
                default:
                    return new Response(WRONG_TYPE, "Unknown request type: " + requestType);
            }
        } catch (Exception e) {
            return new Response(WRONG_DATA, "Error processing request: " + e.getMessage());
        }
    }

    private Response addEmployee(String requestData) {
        Employee employee = parseEmployee(requestData);
        if (employee == null) {
            return new Response(WRONG_DATA, "Invalid employee data");
        }
        company.addEmployee(employee);
        return new Response(ResponseCode.OK, "Employee added successfully");
    }

    private Response getEmployee(String requestData) {
        long id = Long.parseLong(requestData);
        Employee employee = company.getEmployee(id);
        return employee != null
                ? new Response(ResponseCode.OK, employee.toString())
                : new Response(ResponseCode.WRONG_DATA, "Employee not found");
    }

    private Response removeEmployee(String requestData) {
        long id = Long.parseLong(requestData);
        Employee employee = company.removeEmployee(id);
        return employee != null
                ? new Response(ResponseCode.OK, "Employee removed: " + employee)
                : new Response(ResponseCode.WRONG_DATA, "Employee not found");
    }

    private Response getDepartmentBudget(String requestData) {
        int budget = company.getDepartmentBudget(requestData);
        return new Response(ResponseCode.OK, "Department budget: " + budget);
    }

    private Response getDepartments() {
        String[] departments = company.getDepartments();
        return new Response(ResponseCode.OK, Arrays.toString(departments));
    }

    private Response getManagersWithMostFactor() {
        Manager[] managers = company.getManagersWithMostFactor();
        return new Response(ResponseCode.OK, Arrays.toString(managers));
    }

    private Employee parseEmployee(String requestData) {
        try {
            JSONObject jsonObject = new JSONObject(requestData);
            long id = jsonObject.getLong("id");
            int basicSalary = jsonObject.getInt("basicSalary");
            String department = jsonObject.getString("department");

            return new Employee(id, basicSalary, department);
        } catch (Exception e) {
            return null;
        }
    }
}
