import json
import socket
import sys
import time

def build_resp(*args):
    req = [f"*{len(args)}\r\n".encode("ascii")]
    for arg in args:
        b = str(arg).encode("utf-8")
        req.append(f"${len(b)}\r\n".encode("ascii"))
        req.append(b)
        req.append(b"\r\n")
    return b"".join(req)

def main():
    host = "redis"
    port = 6379

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    connected = False
    for attempt in range(15):
        try:
            sock.connect((host, port))
            connected = True
            break
        except Exception:
            time.sleep(1)

    if not connected:
        print(f"Failed to connect to Redis at {host}:{port}", file=sys.stderr)
        sys.exit(1)

    with open("/data/users.json", "r", encoding="utf-8") as f:
        users = json.load(f)

    user_ids = []
    for user in users:
        uid = str(user["id"])
        user_ids.append(uid)
        payload = json.dumps(user, separators=(",", ":"))
        sock.sendall(build_resp("SET", f"user:{uid}", payload))
        sock.recv(1024)

    sock.sendall(build_resp("DEL", "user_ids"))
    sock.recv(1024)
    sock.sendall(build_resp("RPUSH", "user_ids", *user_ids))
    sock.recv(1024)

    sock.close()
    print(f"Successfully seeded {len(users)} users into Redis.")

if __name__ == "__main__":
    main()
