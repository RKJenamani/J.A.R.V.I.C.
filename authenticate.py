import mysql.connector as ms

class authentication():

	def __init__(self,password):
		self.passwd=password
	
	def signup(self, values):
		name=values[0]
		username=values[1]
		password=values[2]
		contact=values[3]
		name=name.strip('\n')
		username=username.strip('\n')
		password=password.strip('\n')
		contact=contact.strip('\n')
		print(" name:",name)
		print(" username:",username)
		print(" password:",password)
		print(" contact:",contact)
		history=""
		mydb = ms.connect(host="localhost",user="root",passwd=self.passwd,database="JARVIC")
		mc=mydb.cursor()
		sql = ("INSERT INTO USERS(NAME, USERNAME, PASSWORD, CONTACT, HISTORY) VALUES (%s, %s,%s, %s, %s)")
		val = (name, username, password, contact, history)
		mc.execute(sql, val)
		mydb.commit()
		

	def login(self, values):
		username=values[0]
		password=values[1]
		username=username.strip('\n')
		password=password.strip('\n')
		print(" username:",username)
		print(" password:",password)
		mydb = ms.connect(host="localhost",user="root",passwd=self.passwd,database="JARVIC")
		mc=mydb.cursor()
		mc.execute("SELECT PASSWORD FROM USERS WHERE USERNAME = \'"+username+"\'")
		sql_return = mc.fetchone()
		if(sql_return==None):
			return 0
		else:
			(database_pass,) = sql_return
			# database_pass=mc.fetchall()
			if(database_pass==password):
				return 1
			else:
				return 0
	def reset_password(self,values):
		username=values[0]
		contact=values[1]
		new_password=values[2]
		username=username.strip('\n')
		new_password=new_password.strip('\n')
		contact=contact.strip('\n')
		mydb = ms.connect(host="localhost",user="root",passwd=self.passwd,database="JARVIC")
		mc=mydb.cursor()
		mc.execute("SELECT CONTACT FROM USERS WHERE USERNAME = \'"+username+"\'")
		sql_return = mc.fetchone()
		if(sql_return==None):
			return 0
		else:
			(database_contact,) = sql_return
			# database_pass=mc.fetchall()
			if(database_contact==contact):
				mycursor = mydb.cursor()
				sql = "UPDATE USERS SET PASSWORD = %s WHERE USERNAME = %s"
				val = (new_password, username)
				mycursor.execute(sql, val)      
				mydb.commit()
				return 1
			else:
				return 0




if __name__ == '__main__':
	jarvic_auth = authentication("software")
	values=["rajat","rajatkj11@gmail.com","software","9582027817"]
	print(jarvic_auth.signup(values))
