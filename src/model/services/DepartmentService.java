package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll() {
		return dao.findAll();

		// temp mock
//		List<Department> list = new ArrayList<Department>();
//		list.add(new Department(1, "Books"));
//		list.add(new Department(2, "Computers"));
//		list.add(new Department(3, "Electronics"));
//		return list;		
	}

	public void saveOrUpdate(Department dep) {
		if (dep.getId() == null) {
			// if id==null -> new department to be created
			dao.insert(dep);
		} else {
			dao.update(dep);
		}
	}

	public void remove(Department dep) {
		dao.deleteById(dep.getId());
	}
}
