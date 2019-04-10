import mysql.connector as ms

class chat_history:

	def __init__(self,password):
		self.passwd=password

	def load_history(self,username):
		mydb = ms.connect(host="localhost",user="root",passwd=self.passwd,database="JARVIC")
		mc=mydb.cursor()
		mc.execute("SELECT HISTORY FROM USERS WHERE USERNAME = \'"+username+"\'")
		sql_return = mc.fetchone()
		print("sql: ",sql_return)
		if(sql_return==None):
			return None
		else:
			(user_history,) = sql_return
			if(user_history==""):
				return None
			user_history=str(user_history)
			print("-",user_history,"-")
			return user_history

	def add_to_history(self,username,who,msg):

		mydb = ms.connect(host="localhost",user="root",passwd=self.passwd,database="JARVIC")
		mc=mydb.cursor()
		mc.execute("SELECT HISTORY FROM USERS WHERE USERNAME = \'"+username+"\'")
		sql_return = mc.fetchone()
		(sql_return,) = sql_return
		print("sql: ",sql_return)
		if(sql_return==""):
			if(who=="u"):
				user_history="u$"+msg
			else:
				user_history="c$"+msg
		else:
			user_history = str(sql_return)
			if(who=="u"):
				user_history=user_history+"$u$"+msg
			else:
				user_history=user_history+"$c$"+msg

		mycursor = mydb.cursor()
		sql = "UPDATE USERS SET HISTORY = %s WHERE USERNAME = %s"
		val = (user_history, username)
		mycursor.execute(sql, val)		
		mydb.commit()


				