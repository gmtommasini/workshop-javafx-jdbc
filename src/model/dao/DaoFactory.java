/* Factory is the only class from the program itself that knows which technology is being used to connect to DB. 
 * If the technology changes, we just need to modify this class, apart of the specific classes for that tech (DB and xxxDaoJDBC).
 * */
package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	}
}
