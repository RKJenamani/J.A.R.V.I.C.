import mysql.connector as ms

class authentication():
	
    def signup(self, values):
        name=values[0]
        username=values[1]
        password=values[2]
        contact=values[3]
        name=name.strip('\n')
        username=username.strip('\n')
        password=password.strip('\n')
        conatct=contact.strip('\n')
        print(" name:",name)
        print(" username:",username)
        print(" password:",password)
        print(" contact:",contact)
        history="u$hello$s$hi$u$bye$u$seeya"
        mydb = ms.connect(host="localhost",user="root",passwd="rajat",database="JARVIC")
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
        mydb = ms.connect(host="localhost",user="root",passwd="rajat",database="JARVIC")
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


if __name__ == '__main__':
	jarvic_auth = authentication()
	values=["rajat","rajatkj11@gmail.com","software","9582027817"]
	print(jarvic_auth.signup(values))
