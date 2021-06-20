package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll(){
		return dao.findAll();
		
		// temp mock
//		List<Department> list = new ArrayList<Department>();
//		list.add(new Department(1, "Books"));
//		list.add(new Department(2, "Computers"));
//		list.add(new Department(3, "Electronics"));
//		return list;		
	}
}
