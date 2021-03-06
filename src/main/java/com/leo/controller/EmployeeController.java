package com.leo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leo.bean.CommonResult;
import com.leo.bean.Employee;
import com.leo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @RequestMapping("/emps")
    public String getEmps(@RequestParam(value = "pn",defaultValue = "1") Integer pn,
                          Model model){

        PageHelper.startPage(pn,5);
        List<Employee> employees=employeeService.getAll();
        PageInfo<Employee> pageInfo=new PageInfo<>(employees,5);
        model.addAttribute("pageInfo",pageInfo);

        return "list";
    }
    @ResponseBody
//    @RequestMapping("/emps")
    public CommonResult getEmpsWithJson(@RequestParam(value = "pn",defaultValue = "1") Integer pn){

        PageHelper.startPage(pn,5);
        List<Employee> employees=employeeService.getAll();
        PageInfo<Employee> pageInfo=new PageInfo<>(employees,5);
        return CommonResult.success().addData("pageInfo",pageInfo);
    }
    @ResponseBody
    @RequestMapping("/addEmp")
    public CommonResult addEmpWithJson(Employee employee){
        employeeService.saveEmp(employee);
        return CommonResult.success();
    }
    @ResponseBody
    @RequestMapping(value = "/checkEmpName",method =RequestMethod.POST)
    public CommonResult checkEmployeeName(@RequestParam("empName") String empName) {
        String regName="(^[a-zA-Z0-9_-]{3,16}$)|(^[\\u2E80-\\u9FFF]+$)";
        if(!empName.matches(regName)){
            return CommonResult.fail("请输入合法的用户名");
        }
        boolean hasName = employeeService.checkEmpName(empName);
        if(hasName){
            return CommonResult.fail("名字已使用");
        }else {
            return CommonResult.success();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/emp/{empId}",method = RequestMethod.GET)
    public CommonResult getEmp(@PathVariable("empId") Integer  empId){
        Employee employee=employeeService.getEmp(empId);
        return CommonResult.success().addData("emp", employee);
    }

    @ResponseBody
    @RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
    public CommonResult updateEmp(Employee  employee){
        employeeService.update(employee);
        return CommonResult.success();
    }
    @ResponseBody
    @RequestMapping(value = "/emp/{empIds}",method = RequestMethod.DELETE)
    public CommonResult deleteEmp(@PathVariable("empIds") String  empIds){

        if(empIds.contains("-")){
            String[] ids=empIds.split("-");
            List<Integer> idList=new ArrayList<>();
            for(String id:ids){
            idList.add(Integer.parseInt(id));
            }
            employeeService.deleteBatch(idList);
        }else {
            employeeService.delete(Integer.parseInt(empIds));
        }
        return CommonResult.success();
    }
}
