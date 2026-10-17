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

    print("Connecting to Redis...", flush=True)
    sock = None
    for attempt in range(1, 21):
        try:
            sock = socket.create_connection((host, port), timeout=3)
            print(f"Connected to Redis at {host}:{port} on attempt {attempt}", flush=True)
            break
        except Exception as e:
            print(f"Attempt {attempt} failed: {e}. Retrying in 1s...", flush=True)
            time.sleep(1)

    if sock is None:
        print(f"Error: Could not connect to {host}:{port}", file=sys.stderr, flush=True)
        sys.exit(1)

    try:
        sock.settimeout(5)
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

        print(f"Successfully seeded {len(users)} users into Redis.", flush=True)
    finally:
        sock.close()

if __name__ == "__main__":
    main()
