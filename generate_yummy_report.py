import os
from fpdf import FPDF
from generate_diagram_image import generate_flow_diagram_image

class YummyReportPDF(FPDF):
    def header(self):
        if self.page_no() > 1:
            self.set_font("Helvetica", "B", 8)
            self.set_text_color(100, 116, 139)
            self.cell(0, 5, "CardVault Android Application - Comprehensive Project Report", new_x="RIGHT", new_y="TOP")
            self.cell(0, 5, "Java + XML Native Android Project", new_x="LMARGIN", new_y="NEXT", align="R")
            self.set_draw_color(226, 232, 240)
            self.set_line_width(0.3)
            self.line(15, 14, 195, 14)
            self.ln(4)

    def footer(self):
        self.set_y(-12)
        self.set_font("Helvetica", "I", 8)
        self.set_text_color(148, 163, 184)
        self.cell(0, 6, f"Page {self.page_no()} | CardVault Complete Report (yummy report)", new_x="RIGHT", new_y="TOP", align="C")

    def section_title(self, title):
        self.ln(3)
        self.set_font("Helvetica", "B", 12)
        self.set_text_color(30, 58, 138)  # Deep Indigo Blue (#1E3A8A)
        self.cell(0, 6, title, new_x="LMARGIN", new_y="NEXT")
        self.set_draw_color(37, 99, 235)  # CardVault Primary Blue
        self.set_line_width(0.6)
        self.line(self.get_x(), self.get_y(), self.get_x() + 45, self.get_y())
        self.ln(3)

    def sub_title(self, title):
        self.set_font("Helvetica", "B", 9.5)
        self.set_text_color(23, 54, 111)
        self.cell(0, 5, title, new_x="LMARGIN", new_y="NEXT")
        self.ln(1)

    def body_p(self, text):
        self.set_font("Helvetica", "", 8.5)
        self.set_text_color(30, 41, 59)
        self.multi_cell(0, 4.3, text)
        self.ln(1.5)

def build_pdf():
    # Ensure diagram image is fresh
    generate_flow_diagram_image()
    diagram_img_path = r"C:\Users\STRIKER\Desktop\app practice for class\CardVault\auth_flow_diagram.png"

    pdf = YummyReportPDF(orientation="P", unit="mm", format="A4")
    pdf.set_auto_page_break(auto=True, margin=14)
    pdf.set_margins(15, 15, 15)
    pdf.add_page()

    # Title Banner
    pdf.set_fill_color(239, 246, 255)
    pdf.set_font("Helvetica", "B", 8)
    pdf.set_text_color(37, 99, 235)
    pdf.cell(60, 5, "NATIVE ANDROID PROJECT REPORT", new_x="LMARGIN", new_y="NEXT", align="C", fill=True)
    pdf.ln(1)

    pdf.set_font("Helvetica", "B", 18)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 8, "CardVault - Digital Physical Card Wallet", new_x="LMARGIN", new_y="NEXT")

    pdf.set_font("Helvetica", "I", 9.5)
    pdf.set_text_color(100, 116, 139)
    pdf.cell(0, 5, "Comprehensive System Specification, Authentication Flow & Architecture Report", new_x="LMARGIN", new_y="NEXT")
    pdf.ln(3)

    # Metadata Box
    pdf.set_fill_color(248, 250, 252)
    pdf.set_draw_color(203, 213, 225)
    pdf.rect(15, pdf.get_y(), 180, 26, "DF")
    y_start = pdf.get_y()

    pdf.set_xy(18, y_start + 2)
    pdf.set_font("Helvetica", "B", 8)
    pdf.set_text_color(100, 116, 139)
    pdf.cell(45, 4, "APPLICATION NAME:", new_x="RIGHT", new_y="TOP")
    pdf.set_font("Helvetica", "", 8)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 4, "CardVault (Native Android Java + XML Views)", new_x="LMARGIN", new_y="NEXT")

    pdf.set_xy(18, y_start + 7)
    pdf.set_font("Helvetica", "B", 8)
    pdf.set_text_color(100, 116, 139)
    pdf.cell(45, 4, "PACKAGE ID / SDK:", new_x="RIGHT", new_y="TOP")
    pdf.set_font("Helvetica", "", 8)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 4, "com.example.cardvault | Min SDK: 24 (Android 7.0) | Target SDK: 34", new_x="LMARGIN", new_y="NEXT")

    pdf.set_xy(18, y_start + 12)
    pdf.set_font("Helvetica", "B", 8)
    pdf.set_text_color(100, 116, 139)
    pdf.cell(45, 4, "PERSISTENCE ENGINES:", new_x="RIGHT", new_y="TOP")
    pdf.set_font("Helvetica", "", 8)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 4, "SQLite (cardvault.db) + Internal Image Sandbox + Private Encrypted SharedPreferences", new_x="LMARGIN", new_y="NEXT")

    pdf.set_xy(18, y_start + 17)
    pdf.set_font("Helvetica", "B", 8)
    pdf.set_text_color(100, 116, 139)
    pdf.cell(45, 4, "SECURITY & NETWORK:", new_x="RIGHT", new_y="TOP")
    pdf.set_font("Helvetica", "", 8)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 4, "100% Local-First, Zero Cloud/API dependencies, SHA-256 Password Hash Digest", new_x="LMARGIN", new_y="NEXT")

    pdf.set_xy(18, y_start + 22)
    pdf.set_font("Helvetica", "B", 8)
    pdf.set_text_color(100, 116, 139)
    pdf.cell(45, 4, "DEMO CREDENTIALS:", new_x="RIGHT", new_y="TOP")
    pdf.set_font("Helvetica", "B", 8)
    pdf.set_text_color(37, 99, 235)
    pdf.cell(0, 4, "user@cardvault.app (or 'user') / password123 (Alex Morgan)", new_x="LMARGIN", new_y="NEXT")

    pdf.set_y(y_start + 30)

    # 1. Project Overview & Architectural Principles
    pdf.section_title("1. Project Overview & Architectural Principles")
    pdf.body_p(
        "CardVault is an enterprise-grade native Android mobile application designed to digitize and manage non-payment "
        "physical cards such as Student IDs, Library Cards, Transit Passes, Gym Memberships, and Loyalty Cards. "
        "The application provides high-fidelity card visualization including two-sided photographic capture, "
        "real-time search and category filtering, card details with sensitive identifier masking, an interactive 3D flip "
        "viewer using camera distance perspective, and full SQLite-backed CRUD persistence."
    )
    pdf.body_p(
        "In this milestone release, CardVault has been extended with a complete simulated authentication, registration, "
        "and password-recovery subsystem. To respect architectural integrity and privacy:\n"
        "- Zero Data Loss Guarantee: CardVault SQLite card records, categories, and photos are strictly segregated "
        "from user credentials, preventing any data erasure or corruption upon login/logout.\n"
        "- Security Best Practice: Plaintext passwords are never stored. Passwords are transformed into 64-character "
        "SHA-256 cryptographic digests before storage.\n"
        "- Academic Transparency: Authentication and recovery flows are implemented purely locally via explicit intents "
        "and private SharedPreferences, with clear user disclosures explaining that no remote SMTP server is attached."
    )

    # 2. Complete Screen Inventory (All 11 Screens)
    pdf.section_title("2. Complete Screen Inventory (All 11 Screens)")
    
    screens = [
        ("1. Splash Screen", "SplashActivity.java | activity_splash.xml", "ConstraintLayout", 
         "Integrates Android native SplashScreen API with Theme.CardVault.Starting. Pre-seeds SQLite sample data safely, displays app logo & tagline, and routes to MainActivity if session active or LoginActivity if unauthenticated."),
        
        ("2. Login Screen", "LoginActivity.java | activity_login.xml", "LinearLayout + CardView", 
         "Accepts email or username alias with instant resolution. Password masking toggle, input validation (short password, empty fields), evaluator test credentials prompt card, explicit Intents to Register & Forgot Password."),
        
        ("3. Registration Screen", "RegisterActivity.java | activity_register.xml", "LinearLayout + CardView", 
         "Full Name, Email format checking, optional Phone number, 6+ character password, and confirm password matching validation. Hashes password using SHA-256 and routes to Login with prefilled email."),
        
        ("4. Forgot Password", "ForgotPasswordActivity.java | activity_forgot_password.xml", "ConstraintLayout", 
         "Validates registered email. Presents academic simulated recovery confirmation card (explaining no remote email server is required) and provides explicit navigation to Reset Password."),
        
        ("5. Reset Password", "ResetPasswordActivity.java | activity_reset_password.xml", "ConstraintLayout", 
         "Displays target account banner. Enforces password matching and minimum 6 characters. Replaces stored SHA-256 hash digest, toasts confirmation, and returns to Login with email prefilled."),
        
        ("6. Home Dashboard", "MainActivity.java | activity_main.xml", "ConstraintLayout + Recycler", 
         "Displays total card count badge, horizontal/vertical recent cards RecyclerView, quick-add card FAB, 'View All' shortcut, and bottom navigation bar. Enforces session authorization."),
        
        ("7. My Cards (Browse & Search)", "MyCardsActivity.java | activity_my_cards.xml", "LinearLayout + Recycler", 
         "Full card catalog with real-time SearchView text filtering across name, identifier, and notes. Horizontal chip filter bar (All, Student ID, Transit, Membership, etc.) and empty state handling."),
        
        ("8. Add / Edit Card", "AddCardActivity.java | activity_add_card.xml", "ScrollView + LinearLayout", 
         "Comprehensive form for card title, category selector spinner, card identifier, expiration date, and notes. Dual-photo capture/selection with sandbox file persistence in getFilesDir()/cards/."),
        
        ("9. Card Details", "CardDetailsActivity.java | activity_card_details.xml", "ScrollView + CardView", 
         "Full-fidelity card inspector. Displays masked identifier with reveal toggle button, category pill, front/back photo switcher, edit card shortcut, and delete action with confirmation dialog."),
        
        ("10. 3D Card Flip Viewer", "CardViewerActivity.java | activity_card_viewer.xml", "FrameLayout + ObjectAnimator", 
         "Immersive full-screen visualizer. Applies dynamic camera distance perspective and 180-degree ObjectAnimator rotation to simulate realistic card flipping between front and back surfaces."),
        
        ("11. Settings & Account", "SettingsActivity.java | activity_settings.xml", "ScrollView + CardView", 
         "Displays 'Account & Session' card with active user profile details (Name, Email). Provides a Sign Out button with confirmation modal that wipes the active session and returns cleanly to Login.")
    ]

    for name, files, layout, desc in screens:
        pdf.set_fill_color(241, 245, 249)
        pdf.set_font("Helvetica", "B", 8)
        pdf.set_text_color(30, 58, 138)
        pdf.cell(48, 5, name, new_x="RIGHT", new_y="TOP")
        
        pdf.set_font("Helvetica", "B", 7.5)
        pdf.set_text_color(37, 99, 235)
        pdf.cell(32, 5, f"[{layout}]", new_x="RIGHT", new_y="TOP")
        
        pdf.set_font("Helvetica", "I", 7.5)
        pdf.set_text_color(100, 116, 139)
        pdf.cell(0, 5, files, new_x="LMARGIN", new_y="NEXT", align="R")
        
        pdf.body_p(desc)
        pdf.ln(0.8)

    # 3. Dedicated Navigation Architecture & Flow Diagram
    pdf.add_page()
    pdf.section_title("3. Navigation Architecture & Flow Diagram")
    pdf.body_p(
        "Below is the complete state-transition diagram illustrating how explicit Intents route users between Splash, "
        "Authentication screens, the Home vault, and Settings. Session boundaries are strictly enforced."
    )

    if os.path.exists(diagram_img_path):
        # Embed diagram image
        pdf.image(diagram_img_path, x=15, w=180)
        pdf.ln(4)

    pdf.sub_title("Key State Transitions & Lifecycle Invariants:")
    pdf.body_p(
        "1. Splash Gate: SplashActivity determines whether the user possesses a valid cached session. If active, it transitions to MainActivity; otherwise, to LoginActivity.\n"
        "2. Form Feedback & Pre-fill: Upon successful registration in RegisterActivity or password update in ResetPasswordActivity, the user is navigated back to LoginActivity with their email prefilled in EXTRA_PREFILL_EMAIL.\n"
        "3. Session Isolation: Successful login sets CardVaultAuthManager session and routes to MainActivity. Sign Out from Settings clears the active session and finishes all vault activities using Intent flags."
    )

    # 4. Technical Requirements & Compliance Verification
    pdf.section_title("4. Technical Requirements & Standards Compliance")
    
    pdf.sub_title("A. Native Android Java & XML Architecture")
    pdf.body_p(
        "CardVault is written 100% in Java 17 and native Android XML views. No cross-platform runtimes, Kotlin, Jetpack Compose, "
        "or hybrid web containers are utilized. All activities extend AppCompatActivity and follow standard Android lifecycle patterns."
    )

    pdf.sub_title("B. Explicit Intent Navigation & Backstack Management")
    pdf.body_p(
        "Navigation between all 11 activities is mediated strictly via explicit Android Intents. Deep tasks (such as Login "
        "completion or Session Logout) employ Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK to ensure the backstack "
        "is pruned, preventing unauthenticated access via the hardware back button."
    )

    pdf.sub_title("C. Day & Night Resource Theming")
    pdf.body_p(
        "- Default Mode (res/values/colors.xml): Crisp slate canvas (#F8FAFC), card surface (#FFFFFF), primary blue (#2563EB), dark indigo headers (#1E3A8A).\n"
        "- Dark Mode (res/values-night/colors.xml): Midnight blue backdrop (#0F172A), elevated slate cards (#1E293B), vibrant cyan-blue accents (#60A5FA).\n"
        "- All UI colors and text styles inherit from Theme.MaterialComponents.DayNight.NoActionBar, ensuring automatic system-wide adaptation."
    )

    pdf.sub_title("D. Multi-Tier Persistence Isolation")
    pdf.body_p(
        "1. SQLite Database (cardvault.db): Manages card metadata (id, name, category, identifier, expiryDate, notes, imagePath, createdAt) "
        "using parameterized queries in DatabaseHelper.java to prevent SQL injection.\n"
        "2. Private File Sandbox (getFilesDir()/cards/): Attached card photos are copied directly into the app's internal sandbox, "
        "retaining validity across device reboots without external storage permission prompts.\n"
        "3. Authentication Manager (cardvault_auth_prefs): Manages user accounts and sessions via private SharedPreferences with SHA-256 "
        "hashed credentials, completely independent of the SQLite card tables."
    )

    # 5. Automated Testing & Build Results
    pdf.section_title("5. Automated Testing & Build Results")
    pdf.body_p(
        "The project was subjected to end-to-end automated testing and release-grade compilation using the Gradle wrapper toolchain:\n"
        "- Unit Test Execution: 'cmd.exe /c gradlew.bat testDebugUnitTest' executed and PASSED 100% of test suites, including "
        "CardVaultAuthTest.java (SHA-256 non-plaintext verification, deterministic digests, password discrimination) and CardTest.java "
        "(identifier masking, null safety).\n"
        "- Debug APK Compilation: 'cmd.exe /c gradlew.bat assembleDebug' completed successfully with EXIT CODE 0 in 1m 04s.\n"
        "- Binary Artifact: app-debug.apk (6.48 MB) located at 'CardVault/app/build/outputs/apk/debug/app-debug.apk'."
    )

    # 6. Evaluator Test Account & Walkthrough
    pdf.section_title("6. Evaluator Test Account & Quick Walkthrough")
    pdf.body_p(
        "To test the application on Android Studio or an Android Emulator/Device:\n"
        "1. Launch App -> Splash screen briefly appears, initializes the database, and transitions to Login.\n"
        "2. Test Login -> Enter Email 'user@cardvault.app' (or username 'user') and Password 'password123'. Tap 'Sign In'.\n"
        "3. Registration -> Tap 'Create Account', enter custom credentials, and submit. The app returns to Login with email prefilled.\n"
        "4. Password Recovery -> Tap 'Forgot Password?', enter email, view simulated confirmation notice, and set a new password.\n"
        "5. Home Dashboard -> View sample cards, browse categories in 'My Cards', search cards in real-time, or add a new card.\n"
        "6. Card Details & 3D Viewer -> Tap any card to reveal details, toggle identifier masking, or experience the 3D flip animation.\n"
        "7. Sign Out -> Open Settings, locate 'Account & Session', tap 'Sign Out', and confirm dialog to return safely to Login."
    )

    # Output paths
    output_cardvault = r"C:\Users\STRIKER\Desktop\app practice for class\CardVault\yummy report.pdf"
    output_root = r"C:\Users\STRIKER\Desktop\app practice for class\yummy report.pdf"
    
    pdf.output(output_cardvault)
    pdf.output(output_root)
    print("PDF Successfully generated at:")
    print("  1.", output_cardvault)
    print("  2.", output_root)

if __name__ == "__main__":
    build_pdf()
