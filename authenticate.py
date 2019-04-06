import mysql.connector as ms

class authentication():
	
    def signup(self, values):
        name=values[0]
        username=values[1]
        password=values[2]
        contact=values[3]
        mydb = ms.connect(host="localhost",user="root",passwd="rajat",database="JARVIC")
        mc=mydb.cursor()
        sql = ("INSERT INTO USERS(NAME, USERNAME, PASSWORD, CONTACT) VALUES (%s, %s,%s, %s)")
        val = (name, username, password, contact)
        mc.execute(sql, val)
        mydb.commit()
        

    def login(self, values):
        username=values[0]
        password=values[1]
        mydb = ms.connect(host="localhost",user="root",passwd="rajat",database="JARVIC")
        mc=mydb.cursor()
        mc.execute("SELECT PASSWORD FROM USERS WHERE USERNAME = \'"+username+"\'")
        (database_pass,) = mc.fetchone()
        # database_pass=mc.fetchall()
        if(database_pass==password):
            return "Y"
        else:
            return "N"


if __name__ == '__main__':
	jarvic_auth = authentication()
	values=["rajatkj11@gmail.com","software"]
	print(jarvic_auth.login(values))