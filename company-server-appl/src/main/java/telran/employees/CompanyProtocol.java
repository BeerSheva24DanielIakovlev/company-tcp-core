package telran.employees;

import java.lang.reflect.Method;
import telran.net.Protocol;
import telran.net.Request;
import telran.net.Response;
import telran.net.ResponseCode;

@SuppressWarnings("unused")
public class CompanyProtocol implements Protocol {
    Company company;

    public CompanyProtocol(Company company) {
        this.company = company;
    }

    @Override
    public Response getResponse(Request request) {
        String requestType = request.requestType();
        String requestData = request.requestData();
        Response response = null;
        try {
            Method method = this.getClass().getDeclaredMethod(requestType, String.class);
            method.setAccessible(true);
            response = (Response) method.invoke(this, requestData);
        } catch (NoSuchMethodException e) {
            response = new Response(ResponseCode.WRONG_TYPE, "Unsupported request type: " + requestType);
        } catch (Exception e) {
            response = new Response(ResponseCode.WRONG_DATA, e.getMessage());
        }
        return response;
    }

    private Response addEmployee(String requestData) {
        Employee empl = Employee.getEmployeeFromJSON(requestData);
        company.addEmployee(empl);
        return new Response(ResponseCode.OK, "");
    }

    private Response getEmployee(String requestData) {
        long id = Long.parseLong(requestData);
        Employee empl = company.getEmployee(id);
        if (empl == null) {
            return new Response(ResponseCode.WRONG_DATA, "Employee not found");
        }
        return new Response(ResponseCode.OK, empl.toString());
    }

    private Response removeEmployee(String requestData) {
        long id = Long.parseLong(requestData);
        Employee empl = company.removeEmployee(id);
        if (empl == null) {
            return new Response(ResponseCode.WRONG_DATA, "Employee not found");
        }
        return new Response(ResponseCode.OK, empl.toString());
    }

    private Response getDepartmentBudget(String requestData) {
        int budget = company.getDepartmentBudget(requestData);
        return new Response(ResponseCode.OK, String.valueOf(budget));
    }

    private Response getDepartments(String requestData) {
        String[] departments = company.getDepartments();
        return new Response(ResponseCode.OK, String.join(",", departments));
    }

    private Response getManagersWithMostFactor(String requestData) {
        Manager[] managers = company.getManagersWithMostFactor();
        return new Response(ResponseCode.OK, String.join(",", managers.toString()));
    }
}
