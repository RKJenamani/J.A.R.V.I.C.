from server_class import server

obj = server("localhost", 2000)
obj.initialise_connection()
msg = input("Enter message to send: ")
obj.send_msg(msg)

while True:
    print("User: " + obj.receive_msg())

