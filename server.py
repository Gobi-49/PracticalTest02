import socket
import datetime

# Configurare server
HOST = '0.0.0.0'  # Adresa locală
PORT = 12345        # Portul de ascultare

def start_server():
    # Creare socket
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
        server_socket.bind((HOST, PORT))
        server_socket.listen(5)
        print(f"Server pornit pe {HOST}:{PORT}")

        while True:
            client_socket, address = server_socket.accept()
            with client_socket:
                print(f"Conexiune de la {address}")
                
                # Obținerea orei curente
                current_time = datetime.datetime.now().strftime("%H:%M:%S")
                client_socket.sendall(current_time.encode('utf-8'))
                print(f"Ora trimisă către client: {current_time}")

if __name__ == "__main__":
    start_server()
