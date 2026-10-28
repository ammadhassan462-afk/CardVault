import os
from PIL import Image, ImageDraw, ImageFont

def generate_flow_diagram_image():
    # Diagram text
    diagram_lines = [
        "                    +-------------------------+",
        "                    |      SplashActivity     | (Theme.CardVault.Starting)",
        "                    |   Checks active session |",
        "                    +------------+------------+",
        "                                 |",
        "             +-------------------+-------------------+",
        "             | Session Active                        | No Active Session",
        "             v                                       v",
        " +-----------------------+               +-----------------------+",
        " |      MainActivity     |               |     LoginActivity     |",
        " | (Home / Card Vault)   |               | user@cardvault.app    |",
        " +-----------^-----------+               +-----+-----------+-----+",
        "             |                                 |           |",
        "             | Successful Login                |           |",
        "             | (sets session)                  v           v",
        "             |                        +-----------+     +-----------------------+",
        "             |                        | Register  |     | ForgotPasswordActivity|",
        "             |                        | Activity  |     | (Simulated Notice)    |",
        "             |                        +-----+-----+     +-----------+-----------+",
        "             |                              |                       |",
        "             |                              | Prefilled Intent      | Explicit Intent",
        "             |                              | back to Login         v",
        "             |                              |           +-----------------------+",
        "             |                              |           | ResetPasswordActivity |",
        "             |                              |           | (SHA-256 update)      |",
        "             |                              |           +-----------+-----------+",
        "             |                              |                       | Prefilled Intent",
        "             |                              +----------->-----------+ back to Login",
        "             |",
        " +-----------+-----------+",
        " |    SettingsActivity   |",
        " |   \"Account & Session\" | --(Sign Out Dialog)--> Clears Session --> LoginActivity",
        " +-----------------------+"
    ]

    width = 1200
    line_height = 28
    height = len(diagram_lines) * line_height + 140

    img = Image.new("RGBA", (width, height), (248, 250, 252, 255)) # Slate 50 background
    draw = ImageDraw.Draw(img)

    # Outer border / card container
    draw.rounded_rectangle([15, 15, width - 15, height - 15], radius=16, fill=(255, 255, 255, 255), outline=(203, 213, 225, 255), width=2)
    
    # Header bar
    draw.rounded_rectangle([15, 15, width - 15, 75], radius=16, fill=(239, 246, 255, 255))
    draw.rectangle([15, 55, width - 15, 75], fill=(239, 246, 255, 255))
    draw.line([15, 75, width - 15, 75], fill=(191, 219, 254, 255), width=2)

    # Try loading a monospace font
    font = None
    title_font = None
    subtitle_font = None
    font_names = ["consola.ttf", "cour.ttf", "lucon.ttf", "arial.ttf"]
    for fn in font_names:
        try:
            font = ImageFont.truetype(fn, 16)
            title_font = ImageFont.truetype("arialbd.ttf" if "arial" in fn else "consolab.ttf", 20)
            subtitle_font = ImageFont.truetype("arial.ttf" if "arial" in fn else "consola.ttf", 13)
            break
        except Exception:
            pass

    if font is None:
        font = ImageFont.load_default()
        title_font = font
        subtitle_font = font

    # Draw Title
    draw.text((40, 26), "CardVault Navigation Flow & Intent State Machine", fill=(30, 58, 138), font=title_font)
    draw.text((40, 50), "Explicit Intents, Authentication Gates, and Persistent Session Cycles", fill=(100, 116, 139), font=subtitle_font)

    # Draw diagram lines
    y = 100
    for line in diagram_lines:
        color = (15, 23, 42) # Slate 900
        if "SplashActivity" in line or "MainActivity" in line or "LoginActivity" in line:
            color = (37, 99, 235) # Blue
        elif "Register" in line or "ResetPassword" in line or "ForgotPassword" in line:
            color = (13, 148, 136) # Teal / Cyan
        elif "SettingsActivity" in line or "Sign Out" in line:
            color = (225, 29, 72) # Rose / Red
        elif "|" in line or "+" in line or "-" in line or ">" in line:
            color = (71, 85, 105) # Slate 600

        draw.text((45, y), line, fill=color, font=font)
        y += line_height

    out_path = r"C:\Users\STRIKER\Desktop\app practice for class\CardVault\auth_flow_diagram.png"
    img.save(out_path, "PNG")
    print("Saved flow diagram image at:", out_path)

if __name__ == "__main__":
    generate_flow_diagram_image()
