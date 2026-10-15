package com.example.financebuddy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financebuddy.ui.theme.FinanceBuddyTheme
import org.json.JSONArray
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil

private val categoryNames = listOf(
    "Food & Groceries", "Transport", "Housing & Rent",
    "Bills & Subscriptions", "Shopping", "Entertainment", "Other Expenses"
)

private data class Lesson(
    val title: String,
    val summary: String,
    val paragraphs: List<String>,
    val tip: String,
    val sourceName: String,
    val sourceUrl: String
)

private val lessons = listOf(
    Lesson(
        "Budgeting Basics", "Manage your first paycheque",
        listOf(
            "Begin with reliable take-home income. Record your regular expenses and planned savings. Do not count uncertain bonuses as dependable monthly income.",
            "MoneySense suggests aiming to save at least 20% of your monthly take-home pay, or more if you can. Treat this as a guideline rather than a compulsory target.",
            "Review your budget regularly, including subscriptions, transport, groceries and debt repayments. When circumstances change, revise your plan."
        ),
        "Try FinanceBuddy's Budget Tracker to review spending categories.",
        "MoneySense — Managing Your Money",
        "https://www.moneysense.gov.sg/managing-your-money/"
    ),
    Lesson(
        "Emergency Funds", "Prepare for unexpected expenses",
        listOf(
            "An emergency fund is money set aside for unexpected situations such as urgent repairs, medical costs or a period without employment.",
            "A common MoneySense planning guideline is approximately 3 to 6 months of expenses. Your personal target may be different if your employment or household needs are less predictable.",
            "Keep emergency funds accessible and separate from money intended for longer-term goals. Avoid assuming investments will always be immediately available at their original value."
        ),
        "Compare your monthly expenses against a 3-month and 6-month target.",
        "MoneySense — Basic Financial Planning Guide",
        "https://www.moneysense.gov.sg/planning-your-finances-well/"
    ),
    Lesson(
        "CPF Explained", "Understand your CPF accounts",
        listOf(
            "For eligible Singapore employees, CPF contributions come from both the employee and employer. The employee share is deducted from wages; the employer share is an additional employer contribution.",
            "Before age 55, CPF savings generally go into the Ordinary Account (OA), Special Account (SA) and MediSave Account (MA). At age 55, a Retirement Account (RA) is created and the Special Account is closed.",
            "CPF contribution rates depend on factors including age, wages and citizenship or Permanent Resident status. Do not apply a single rate to everyone. Use CPF Board's official calculator for individual figures."
        ),
        "Your FinanceBuddy income field asks for take-home pay, so do not deduct CPF again.",
        "CPF Board — CPF Overview",
        "https://www.cpf.gov.sg/member/cpf-overview"
    ),
    Lesson(
        "Needs vs Wants", "Think before spending",
        listOf(
            "Needs are essential costs such as necessary food, basic transport and required housing expenses. Wants are discretionary purchases such as optional upgrades, entertainment and extra subscriptions.",
            "The same purchase may be a need for one person but a want for another. For example, travel required for work differs from an optional leisure trip.",
            "Start by checking whether your essential bills are covered, then decide how much remains for goals and discretionary spending."
        ),
        "Try reviewing the Shopping and Entertainment categories in Budget Tracker.",
        "MoneySense — Managing Your Money",
        "https://www.moneysense.gov.sg/managing-your-money/"
    ),
    Lesson(
        "Financial Scam Awareness", "Check before committing money",
        listOf(
            "Be cautious of investments promising unusually high returns with little or no risk. All investments involve risk.",
            "Check financial institutions and representatives through official MAS resources. Verify the identity of anyone claiming to represent a regulated institution using official contact channels.",
            "Avoid pressure to transfer money immediately. If you suspect a scam, contact your bank promptly and report it to the relevant authorities.",
            "In Singapore, ScamShield can help you check suspicious phone numbers, messages and website links. You can also submit suspicious content using the ScamShield app.",
            "If you are unsure whether something is a scam, call the 24/7 ScamShield Helpline at 1799. If you have lost money, contact your bank immediately and make a police report. A ScamShield app report is not an official police report."
        ),
        "Pause before clicking links or transferring money. Open ScamShield through its official website or app store listing to check suspicious content.",
        "MoneySense — How to Spot an Investment Scam",
        "https://www.moneysense.gov.sg/how-to-spot-an-investment-scam/"
    ),
    Lesson(
        "Credit Cards & Debt Management", "Borrow responsibly and avoid costly debt",
        listOf(
            "A credit card is a form of borrowing, not additional income. If you do not pay the full statement amount by the due date, interest and fees may apply according to your card terms.",
            "Paying only the minimum can extend repayment and increase total costs. If you have multiple debts, reviewing the highest-interest debts first can help reduce interest expenses.",
            "Buy Now, Pay Later plans can also create obligations. Before committing, check repayment dates, charges, and whether your monthly budget can cover the payments."
        ),
        "Use the debt illustration below to see the effect of interest on an unpaid balance. Actual credit card rules and interest calculations differ by provider.",
        "MoneySense — Understanding Credit Card Interest and Charges",
        "https://www.moneysense.gov.sg/understanding-credit-card-interest-and-charges/"
    ),
    Lesson(
        "Investing Basics", "Understand risk, returns and compounding",
        listOf(
            "Saving is useful for short-term needs and emergency money. Investing aims for potential long-term growth, but investments can lose value and returns are never guaranteed.",
            "Before investing, think about your financial goals, time horizon, emergency savings, and how much loss you could afford. Diversification can reduce concentration risk, but does not remove all risk.",
            "Compounding means growth can generate further growth over time. A projection based on assumed returns is only an illustration, not a forecast or product recommendation."
        ),
        "Use the investing illustration to compare contributions over 5, 10 and 20 years under different hypothetical rates.",
        "MoneySense — What Is Investing?",
        "https://www.moneysense.gov.sg/investments/what-is-investing/"
    ),
    Lesson(
        "Insurance Basics", "Understand financial protection",
        listOf(
            "Insurance transfers certain financial risks to an insurer in exchange for premiums. Health, life, and general insurance protect against different events; the exact benefits depend on the policy.",
            "Before buying insurance, consider your needs, dependants, existing coverage and affordability. Read exclusions, waiting periods, deductibles, co-payments and claims conditions rather than comparing premiums alone.",
            "For Singapore graduates, review existing workplace coverage and relevant national schemes before deciding whether extra protection is needed."
        ),
        "Explore the simplified medical-bill example below, then check your actual policy document to understand coverage and exclusions.",
        "MoneySense — Insurance Basics",
        "https://www.moneysense.gov.sg/insurance-basics/"
    )
)

private data class QuizQuestion(
    val question: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val practicalTip: String
)

private val quizQuestions = listOf(
    QuizQuestion(
        "You receive your first S$1,800 take-home salary. What should you do first?",
        listOf("Spend it freely and rely on next month's pay", "Plan essentials, savings and optional spending", "Invest everything immediately", "Apply for several credit cards"),
        1,
        "Planning around take-home income helps you cover essential bills and make room for savings. Spending everything immediately may leave nothing for unexpected needs.",
        "Before spending your first salary, check fixed bills, set an achievable savings target, and decide how much is available for wants."
    ),
    QuizQuestion(
        "Your work laptop suddenly needs an essential S$600 repair. You have accessible emergency savings. What is a sensible first option?",
        listOf("Ignore the problem", "Consider using emergency savings and plan to replenish them", "Buy a much more expensive laptop on impulse", "Borrow without checking terms"),
        1,
        "An emergency fund exists to handle necessary, unexpected costs without automatically relying on expensive credit. The best response depends on how urgent the expense is and the available alternatives.",
        "After using emergency money, make a realistic plan to restore your emergency buffer."
    ),
    QuizQuestion(
        "An eligible Singapore Citizen employee aged 25 earns S$3,000 gross ordinary monthly wages. Why is take-home pay usually lower?",
        listOf("An employee CPF contribution is deducted", "Employer CPF is subtracted from take-home pay", "CPF is contributed only annually", "All wages go into MediSave"),
        0,
        "Employee CPF contributions are deducted from gross wages, while employer CPF contributions are paid separately by the employer. Applicable rates depend on employee circumstances.",
        "Use the CPF Calculator & Guide to distinguish gross salary from take-home salary."
    ),
    QuizQuestion(
        "You cut optional purchases by S$80 every month and save the difference. How much extra could you save in one year, excluding interest?",
        listOf("S$80", "S$480", "S$960", "S$9,600"),
        2,
        "Twelve months of S$80 saved per month gives S$80 × 12 = S$960. This assumes you consistently save the amount rather than spending it elsewhere.",
        "Use Lifestyle Spending to identify real habits you can adjust, then use Savings Simulator to explore longer-term totals."
    ),
    QuizQuestion(
        "Someone online promises guaranteed high investment returns and asks for an urgent transfer. What should you do?",
        listOf("Send a small test amount", "Check through official sources and avoid rushing", "Share banking login details", "Rely only on positive online reviews"),
        1,
        "Guaranteed high returns with no risk and urgent payment pressure are scam warning signs. Independent checks are essential and you should never share banking credentials.",
        "Use official MAS resources and ScamShield. If money has been transferred, contact your bank immediately."
    ),
    QuizQuestion(
        "You pay only the minimum amount on an outstanding credit card bill. What could happen?",
        listOf("The remaining balance may keep attracting interest", "The debt disappears", "Interest can never be charged", "Your CPF automatically repays it"),
        0,
        "Making only the minimum payment may keep the account current, but the remaining balance can continue accumulating interest and take longer to repay.",
        "Aim to pay eligible credit card purchases in full by the due date when possible, and read the issuer's terms."
    ),
    QuizQuestion(
        "Before investing money you may need next month, which consideration is most important?",
        listOf("The biggest advertised return", "Whether you can afford a loss and access your money when needed", "A friend's latest stock tip", "How popular the investment is online"),
        1,
        "Investments can lose value, especially over short periods. Your investment horizon, accessible savings and ability to absorb losses should guide decisions.",
        "Build a suitable emergency buffer before putting money needed soon into risky investments."
    ),
    QuizQuestion(
        "When comparing two insurance plans, what should you review besides premiums?",
        listOf("Only the app colour", "Benefits, exclusions, deductibles and co-payments", "Only advertisements", "How many friends bought the plan"),
        1,
        "Low premiums do not necessarily mean better coverage. Limits, exclusions and out-of-pocket costs determine how a policy works when you claim.",
        "Review your existing coverage and read policy terms before deciding whether extra protection is needed."
    )
)

private val quizTopics = listOf(
    "First salary budgeting", "Emergency funds", "CPF understanding",
    "Lifestyle spending", "Scam awareness", "Credit cards and debt",
    "Investing basics", "Insurance protection"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceBuddyTheme {
                Surface(Modifier.fillMaxSize()) { FinanceBuddyApp() }
            }
        }
    }
}

@Composable
fun FinanceBuddyApp() {
    // 0 Welcome, 1 Profile, 2 Finances, 3 Goals, 4 Dashboard,
    // 5 Budget Tracker, 6 Simulator, 7 Learning Hub, 8 Lesson, 9 Quiz,
    // 10 Real-Life Scenarios, 11 CPF Calculator
    // Financial records are stored privately on this Android device.
    // SharedPreferences avoids extra Gradle dependencies for this prototype.
    val context = LocalContext.current
    val localStorage = remember(context) {
        context.getSharedPreferences("financebuddy_local_data", Context.MODE_PRIVATE)
    }
    val restoredCategories = remember(localStorage) {
        try {
            val array = JSONArray(localStorage.getString("categories", "[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]"))
            if (array.length() == categoryNames.size) {
                List(categoryNames.size) { index -> array.optString(index, "0") }
            } else List(categoryNames.size) { "0" }
        } catch (_: Exception) {
            List(categoryNames.size) { "0" }
        }
    }
    var setupComplete by rememberSaveable {
        mutableStateOf(localStorage.getBoolean("setup_complete", false))
    }
    var screen by rememberSaveable { mutableIntStateOf(if (setupComplete) 4 else 0) }
    var name by rememberSaveable { mutableStateOf(localStorage.getString("name", "") ?: "") }
    var experience by rememberSaveable {
        mutableStateOf(localStorage.getString("experience", "Less than 1 year") ?: "Less than 1 year")
    }
    var employment by rememberSaveable {
        mutableStateOf(localStorage.getString("employment", "Full-time employed") ?: "Full-time employed")
    }
    var income by rememberSaveable { mutableStateOf(localStorage.getString("income", "") ?: "") }
    var expenses by rememberSaveable { mutableStateOf("") }
    var savings by rememberSaveable { mutableStateOf(localStorage.getString("savings", "") ?: "") }
    var budgetOnboarding by rememberSaveable { mutableStateOf(false) }
    var editingFinances by rememberSaveable { mutableStateOf(false) }
    var goal by rememberSaveable {
        mutableStateOf(localStorage.getString("goal", "Emergency Fund") ?: "Emergency Fund")
    }
    var target by rememberSaveable { mutableStateOf(localStorage.getString("target", "") ?: "") }
    var contribution by rememberSaveable { mutableStateOf(localStorage.getString("contribution", "") ?: "") }
    // Portion of existing total savings reserved specifically for this goal.
    var goalAllocated by rememberSaveable {
        mutableStateOf(localStorage.getString("goal_allocated", "0") ?: "0")
    }
    var savedCategories by rememberSaveable { mutableStateOf(restoredCategories) }
    var draftCategories by rememberSaveable { mutableStateOf(restoredCategories) }
    var hasSavedBudget by rememberSaveable {
        mutableStateOf(localStorage.getBoolean("has_saved_budget", false))
    }

    // Save updated profile, budget and goal data automatically after each UI update.
    // Saves locally; internet access, user accounts and Firebase are not required.
    SideEffect {
        localStorage.edit()
            .putBoolean("setup_complete", setupComplete)
            .putString("name", name)
            .putString("experience", experience)
            .putString("employment", employment)
            .putString("income", income)
            .putString("savings", savings)
            .putString("goal", goal)
            .putString("target", target)
            .putString("contribution", contribution)
            .putString("goal_allocated", goalAllocated)
            .putString("categories", JSONArray(savedCategories).toString())
            .putBoolean("has_saved_budget", hasSavedBudget)
            .apply()
    }
    var spendingReduction by rememberSaveable { mutableStateOf("100") }
    var projectionYears by rememberSaveable { mutableIntStateOf(1) }
    var selectedLesson by rememberSaveable { mutableIntStateOf(0) }
    var questionIndex by rememberSaveable { mutableIntStateOf(0) }
    var quizScore by rememberSaveable { mutableIntStateOf(0) }
    var quizResults by rememberSaveable { mutableStateOf(List(quizQuestions.size) { false }) }
    var selectedAnswer by rememberSaveable { mutableIntStateOf(-1) }
    var answerChecked by rememberSaveable { mutableStateOf(false) }
    var quizFinished by rememberSaveable { mutableStateOf(false) }
    var selectedScenario by rememberSaveable { mutableIntStateOf(0) }
    var flashcardRevealed by rememberSaveable { mutableStateOf(false) }

    val monthlyIncome = income.toDoubleOrNull() ?: 0.0
    val monthlyExpenses = if (hasSavedBudget) savedCategories.sumOf { it.toDoubleOrNull() ?: 0.0 } else 0.0
    val currentSavings = savings.toDoubleOrNull() ?: 0.0
    val remainingBalance = monthlyIncome - monthlyExpenses
    val targetAmount = target.toDoubleOrNull() ?: 0.0
    val monthlySaving = contribution.toDoubleOrNull() ?: 0.0
    val allocatedAmount = goalAllocated.toDoubleOrNull() ?: 0.0
    val validAllocation = isValidMoney(goalAllocated) && allocatedAmount <= currentSavings
    val validFinances = listOf(income, savings).all(::isValidMoney)
    val validGoal = isValidMoney(target) && targetAmount > 0 &&
            isValidMoney(contribution) && validAllocation
    val savedTotal = savedCategories.sumOf { it.toDoubleOrNull() ?: 0.0 }
    val validBudget = draftCategories.all(::isValidMoney)
    val draftTotal = draftCategories.sumOf { it.toDoubleOrNull() ?: 0.0 }
    val progress = if (targetAmount > 0) (allocatedAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val amountNeeded = (targetAmount - allocatedAmount).coerceAtLeast(0.0)
    val goalMonths = monthsToTarget(amountNeeded, monthlySaving)

    fun startQuiz() {
        questionIndex = 0
        quizScore = 0
        quizResults = List(quizQuestions.size) { false }
        selectedAnswer = -1
        answerChecked = false
        quizFinished = false
        flashcardRevealed = false
        screen = 9
    }

    val employmentOptions = if (experience == "Not yet employed") {
        listOf("Currently job-seeking")
    } else {
        listOf("Full-time employed", "Part-time employed", "Currently job-seeking")
    }

    when (screen) {
        0 -> Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.financebuddy_logo),
                contentDescription = "FinanceBuddy Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Your Personal Financial Companion")
            Spacer(Modifier.height(32.dp))
            Button(onClick = { screen = 1 }) { Text("Get Started") }
        }

        1 -> Page {
            ScreenHeading("Let's Get to Know You!", "Step 1 of 3 - Personal Profile")
            Spacer(Modifier.height(24.dp))
            Text("What should we call you?")
            OutlinedTextField(name, { name = it }, label = { Text("Preferred Name") },
                singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Text("How long have you been working?", fontWeight = FontWeight.Bold)
            RadioChoices(listOf("Not yet employed", "Less than 1 year", "1-2 years", "2-3 years"), experience) {
                experience = it
                if (it == "Not yet employed") {
                    employment = "Currently job-seeking"
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Employment Status", fontWeight = FontWeight.Bold)
            RadioChoices(employmentOptions, employment) { employment = it }
            if (experience == "Not yet employed" || employment == "Currently job-seeking") {
                Spacer(Modifier.height(8.dp))
                Text("You can continue with S$0 income and S$0 planned savings. Enter your actual expenses and available savings.", fontSize = 13.sp)
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { editingFinances = false; screen = 2 }, enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Continue") }
            TextButton(onClick = { screen = if (validFinances && validGoal) 4 else 0 }) { Text("Back") }
        }

        2 -> Page {
            ScreenHeading("Your Monthly Finances", "Step 2 of 3 - Financial Details")
            Spacer(Modifier.height(16.dp))
            Text("Hi $name! Let's understand your finances.")
            if (experience == "Not yet employed" || employment == "Currently job-seeking") {
                Spacer(Modifier.height(8.dp))
                Text("If you have no current earnings, enter 0 for income. You can still record expenses and any savings.", fontSize = 13.sp)
            }
            Spacer(Modifier.height(24.dp))
            Text("Monthly Take-Home Income (S$)", fontWeight = FontWeight.Bold)
            FinanceInputField(income, { income = it }, "Enter monthly income")
            Spacer(Modifier.height(20.dp))
            Text("Current Savings (S$)", fontWeight = FontWeight.Bold)
            FinanceInputField(savings, { savings = it }, "Enter current savings")
            Spacer(Modifier.height(18.dp))
            Text("Your monthly expenses will be calculated from the Budget Tracker. You only need to enter them once.",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (hasSavedBudget) {
                Spacer(Modifier.height(12.dp))
                Text("Current saved monthly expenses: ${formatSGD(monthlyExpenses)}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (editingFinances) {
                        editingFinances = false
                        screen = 4
                    } else {
                        draftCategories = savedCategories.toList()
                        budgetOnboarding = true
                        screen = 5
                    }
                },
                enabled = validFinances,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (editingFinances) "Save Financial Details" else "Continue to Budget Tracker") }
            TextButton(onClick = { screen = 1 }) { Text("Back to Profile") }
        }

        3 -> Page {
            ScreenHeading("Set Your Financial Goals", "Step 3 of 3 - Financial Goals")
            Spacer(Modifier.height(16.dp))
            Text("Hi $name! Let's plan your financial future.")
            Spacer(Modifier.height(24.dp))
            Text("What are you saving for?", fontWeight = FontWeight.Bold)
            RadioChoices(listOf("Emergency Fund", "Travel", "Housing", "Education", "Other"), goal) { goal = it }
            Spacer(Modifier.height(24.dp))
            Text("Target Savings Amount (S$)", fontWeight = FontWeight.Bold)
            FinanceInputField(target, { target = it }, "Enter your savings goal")
            Spacer(Modifier.height(20.dp))
            Text("Savings Allocated to This Goal (S$)", fontWeight = FontWeight.Bold)
            FinanceInputField(goalAllocated, { goalAllocated = it }, "From your current savings")
            Spacer(Modifier.height(8.dp))
            Text("Available from your total savings: ${formatSGD(currentSavings)}", fontSize = 13.sp)
            if (!validAllocation) Text(
                "Allocated savings must be between S$0 and ${formatSGD(currentSavings)}.",
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(20.dp))
            Text("Planned Monthly Savings Towards This Goal (S$)", fontWeight = FontWeight.Bold)
            FinanceInputField(contribution, { contribution = it }, "Amount to save each month")
            Spacer(Modifier.height(12.dp))
            Text(if (hasSavedBudget) "Available monthly balance: ${formatSGD(remainingBalance)}" else "Complete Budget Tracker for an accurate monthly balance.")
            if (hasSavedBudget && validGoal && monthlySaving > remainingBalance) Text(
                "Your planned savings exceed your available monthly balance.",
                color = MaterialTheme.colorScheme.error
            )
            if (validGoal) {
                Spacer(Modifier.height(24.dp))
                SavingsProgressCard(goal, allocatedAmount, targetAmount, progress, amountNeeded, goalMonths)
            }
            if (goal == "Emergency Fund" && validFinances && hasSavedBudget) {
                Spacer(Modifier.height(20.dp))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Emergency Fund Guide", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Text("A common guideline is 3 to 6 months of essential expenses.")
                        Spacer(Modifier.height(12.dp))
                        Text("3-Month Target: ${formatSGD(monthlyExpenses * 3)}")
                        Text("6-Month Target: ${formatSGD(monthlyExpenses * 6)}")
                        Spacer(Modifier.height(8.dp))
                        Text("This uses total monthly expenses as an approximation.", fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { setupComplete = true; screen = 4 },
                enabled = validFinances && validGoal && (!hasSavedBudget || monthlySaving <= remainingBalance.coerceAtLeast(0.0) || (monthlyIncome == 0.0 && monthlySaving == 0.0)),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Finish Setup") }
            TextButton(onClick = { budgetOnboarding = true; draftCategories = savedCategories.toList(); screen = 5 }) { Text("Back to Budget Tracker") }
        }

        4 -> Page {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hey, $name! 👋", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Here's your financial overview.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.width(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.financebuddy_logo),
                    contentDescription = "FinanceBuddy Logo",
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(Modifier.height(24.dp))

            if (hasSavedBudget) {
                FinancialHealthPanel(
                    income = monthlyIncome, expenses = monthlyExpenses,
                    plannedSavings = monthlySaving, allocatedSavings = allocatedAmount,
                    hasSavedBudget = true
                )
                Spacer(Modifier.height(16.dp))
                MonthlyOverviewPanel(
                    income = monthlyIncome, expenses = monthlyExpenses,
                    plannedSavings = monthlySaving
                )
            } else {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Complete Your Budget", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Expenses: Not set. Add your spending categories to calculate your balance and Financial Health score.")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { budgetOnboarding = false; draftCategories = savedCategories.toList(); screen = 5 },
                            modifier = Modifier.fillMaxWidth()) { Text("Complete Your Budget") }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            BudgetBreakdownPanel(
                hasSavedBudget = hasSavedBudget,
                savedCategories = savedCategories,
                totalExpenses = monthlyExpenses,
                onOpenBudget = {
                    budgetOnboarding = false
                    draftCategories = savedCategories.toList()
                    screen = 5
                }
            )
            Spacer(Modifier.height(22.dp))

            Text("Your Savings Goal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(goal, fontWeight = FontWeight.SemiBold)
                        Text("${formatCompactSGD(allocatedAmount)} / ${formatCompactSGD(targetAmount)}", fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Text("${String.format(Locale.US, "%.1f", progress * 100)}% completed · ${formatSGD(amountNeeded)} remaining", fontSize = 12.sp)
                    Text(when {
                        goalMonths == 0 -> "Goal achieved"
                        goalMonths != null -> "Estimated time: $goalMonths months"
                        else -> "Set a monthly contribution to estimate time"
                    }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(22.dp))
            Text("Main Financial Tools", fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardTile("Budget Tracker", "Manage expenses", Modifier.weight(1f)) {
                    budgetOnboarding = false
                    draftCategories = savedCategories.toList()
                    screen = 5
                }
                DashboardTile("Savings Simulator", "Explore future savings", Modifier.weight(1f)) {
                    spendingReduction = "100"
                    projectionYears = 1
                    screen = 6
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardTile("Financial Goals", "Track savings targets", Modifier.weight(1f)) { screen = 3 }
                DashboardTile("CPF Calculator", "Calculate CPF contributions", Modifier.weight(1f)) { screen = 11 }
            }
            Spacer(Modifier.height(24.dp))
            Text("Learn & Practise", fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Column(Modifier.fillMaxWidth()) {
                DashboardLink("Financial Learning Hub", "8 financial literacy lessons") { screen = 7 }
                Spacer(Modifier.height(8.dp))
                DashboardLink("Financial Literacy Quiz", "Test your financial knowledge") { startQuiz() }
                Spacer(Modifier.height(8.dp))
                DashboardLink("Real-Life Scenarios", "Make decisions and see the consequences") {
                    selectedScenario = 0
                    screen = 10
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Manage Your Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { screen = 1 }, modifier = Modifier.fillMaxWidth()) {
                Text("Edit Personal Profile")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { editingFinances = true; screen = 2 }, modifier = Modifier.fillMaxWidth()) {
                Text("Edit Income & Savings")
            }
            Spacer(Modifier.height(16.dp))
            Text("Financial figures and health scores are educational estimates, not financial advice.",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        5 -> Page {
            ScreenHeading("Budget Tracker", "Know where your money goes.")
            Spacer(Modifier.height(24.dp))
            SummaryCard("Total Categorised Expenses", formatSGD(draftTotal)) {
                Text("Monthly Income: ${formatSGD(monthlyIncome)}")
                Text("Remaining Balance: ${formatSGD(monthlyIncome - draftTotal)}")
                if (draftTotal > monthlyIncome) Text("Warning: Expenses exceed income.", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(20.dp))
            if (budgetOnboarding) {
                Text("Step 3 of 4 — Budget Tracker. Add your expense categories, or skip and complete them later.",
                    fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
            }
            Text("Monthly Spending Categories", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Enter how much you spend each month.")
            Spacer(Modifier.height(20.dp))
            categoryNames.forEachIndexed { index, label ->
                CategoryInput(label, draftCategories[index]) { value ->
                    draftCategories = draftCategories.toMutableList().also { it[index] = value }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Spending Breakdown", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            categoryNames.forEachIndexed { index, label ->
                SpendingBar(label, draftCategories[index].toDoubleOrNull() ?: 0.0, draftTotal)
            }
            Spacer(Modifier.height(20.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Budgeting Tip", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Identify essential expenses first, then review optional spending for possible savings.")
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                if (validBudget) {
                    savedCategories = draftCategories.toList()
                    hasSavedBudget = true
                    val nextScreen = if (budgetOnboarding) 3 else 4
                    budgetOnboarding = false
                    screen = nextScreen
                }
            }, enabled = validBudget, modifier = Modifier.fillMaxWidth()) { Text(if (budgetOnboarding) "Save Budget & Continue" else "Save Budget & Return to Dashboard") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {
                draftCategories = savedCategories.toList()
                val nextScreen = if (budgetOnboarding) 3 else 4
                budgetOnboarding = false
                screen = nextScreen
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (budgetOnboarding) "Skip for Now" else "Cancel Without Saving")
            }
        }

        6 -> {
            if (!hasSavedBudget) {
                Page {
                    ScreenHeading("Savings Simulator", "Complete your Budget Tracker first")
                    Spacer(Modifier.height(16.dp))
                    Text("Your monthly expenses are not set. Enter your spending categories before calculating savings scenarios.")
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { budgetOnboarding = false; draftCategories = savedCategories.toList(); screen = 5 },
                        modifier = Modifier.fillMaxWidth()) { Text("Open Budget Tracker") }
                    OutlinedButton(onClick = { screen = 4 }, modifier = Modifier.fillMaxWidth()) { Text("Back to Dashboard") }
                }
            } else {
                val reduction = spendingReduction.toDoubleOrNull() ?: 0.0
                val validReduction = isValidMoney(spendingReduction) && reduction <= monthlyExpenses
                val scenarioMonthlySaving = monthlySaving + reduction
                val baseProjection = currentSavings + monthlySaving * projectionYears * 12
                val scenarioProjection = currentSavings + scenarioMonthlySaving * projectionYears * 12
                val extra = reduction * projectionYears * 12
                Page {
                    ScreenHeading("Savings Simulator", "See how small changes add up.")
                    Spacer(Modifier.height(12.dp))
                    Text("Explore what happens if you reduce your monthly spending and save the difference.")
                    Spacer(Modifier.height(24.dp))
                    SummaryCard("Your Current Financial Situation", formatSGD(monthlySaving)) {
                        Text("Planned monthly savings")
                        Text("Income: ${formatSGD(monthlyIncome)}")
                        Text("Expenses: ${formatSGD(monthlyExpenses)}")
                        Text("Available balance: ${formatSGD(remainingBalance)}")
                        Text("Starting total savings: ${formatSGD(currentSavings)}")
                        Text("Savings allocated to $goal: ${formatSGD(allocatedAmount)}")
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("What if you reduce monthly spending?", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    FinanceInputField(spendingReduction, { spendingReduction = it }, "Monthly spending reduction (S$)")
                    Spacer(Modifier.height(8.dp))
                    Text("You currently spend ${formatSGD(monthlyExpenses)} per month.", fontSize = 13.sp)
                    if (!validReduction) Text("Enter a reduction between S$0 and ${formatSGD(monthlyExpenses)}.", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(24.dp))
                    Text("Projection Period", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 3, 5).forEach { years ->
                            FilterChip(selected = projectionYears == years, onClick = { projectionYears = years }, label = { Text(if (years == 1) "1 Year" else "$years Years") })
                        }
                    }
                    if (validReduction) {
                        Spacer(Modifier.height(24.dp))
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Your New Scenario", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(14.dp))
                                Text("Current monthly savings plan:")
                                Text(formatSGD(monthlySaving), fontSize = 23.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))
                                Text("New monthly savings if spending is reduced:")
                                Text(formatSGD(scenarioMonthlySaving), fontSize = 25.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(12.dp))
                                Text("New monthly expenses: ${formatSGD(monthlyExpenses - reduction)}")
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Savings Projection", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Projected total savings after $projectionYears ${if (projectionYears == 1) "year" else "years"}:")
                        Spacer(Modifier.height(18.dp))
                        val maximum = maxOf(baseProjection, scenarioProjection, 1.0)
                        ProjectionBar("Current Plan", baseProjection, maximum)
                        ProjectionBar("New Scenario", scenarioProjection, maximum)
                        Spacer(Modifier.height(16.dp))
                        SummaryCard("Potential Additional Savings", formatSGD(extra)) {
                            Text("Over $projectionYears ${if (projectionYears == 1) "year" else "years"}")
                            Spacer(Modifier.height(8.dp))
                            Text("Assumes you consistently save the entire spending reduction.")
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Compare Different Time Periods", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        listOf(1, 3, 5).forEach { years ->
                            val normal = currentSavings + monthlySaving * years * 12
                            val simulated = currentSavings + scenarioMonthlySaving * years * 12
                            Card(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("$years ${if (years == 1) "Year" else "Years"}", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Current: ${formatSGD(normal)}")
                                    Text("Scenario: ${formatSGD(simulated)}")
                                    Text("Extra: ${formatSGD(simulated - normal)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (targetAmount > 0) {
                            Spacer(Modifier.height(16.dp))
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("Impact on Your Financial Goal", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(10.dp))
                                    Text("Goal: $goal")
                                    Text("Target: ${formatSGD(targetAmount)}")
                                    Text("Already allocated: ${formatSGD(allocatedAmount)}")
                                    Spacer(Modifier.height(10.dp))
                                    val months = monthsToTarget(amountNeeded, scenarioMonthlySaving)
                                    Text(when {
                                        amountNeeded <= 0 -> "Your savings allocated to this goal already meet its target."
                                        months != null -> "If you direct your planned savings and the extra spending reduction to this goal, the target could be reached in $months months."
                                        else -> "No positive monthly savings are projected."
                                    })
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(18.dp)) {
                            Text("Important Assumptions", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("This simulator assumes constant monthly income, expenses and savings contributions, with no interest, inflation or withdrawals.")
                            Spacer(Modifier.height(8.dp))
                            Text("Educational estimates only; not a prediction of actual future returns.", fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(onClick = {
                        spendingReduction = "0"
                        projectionYears = 1
                    }, modifier = Modifier.fillMaxWidth()) { Text("Reset Simulation") }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { screen = 4 }, modifier = Modifier.fillMaxWidth()) { Text("Back to Dashboard") }
                }
            } // End savings simulator when budget exists
        }

        // =================================
        // SCREEN 7: LEARNING HUB
        // =================================
        7 -> Page {
            ScreenHeading("Financial Learning Hub", "Short lessons for fresh graduates in Singapore.")
            Spacer(Modifier.height(12.dp))
            Text("Learn at your own pace using educational guidance and links to official sources.")
            Spacer(Modifier.height(24.dp))
            lessons.forEachIndexed { index, lesson ->
                Card(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(Modifier.padding(18.dp)) {
                        Text(lesson.title, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(lesson.summary, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(14.dp))
                        Button(onClick = {
                            selectedLesson = index
                            screen = 8
                        }, modifier = Modifier.fillMaxWidth()) { Text("Read Lesson") }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { screen = 4 }, modifier = Modifier.fillMaxWidth()) { Text("Back to Dashboard") }
            Spacer(Modifier.height(8.dp))
            Text("FinanceBuddy is independent and is not affiliated with MoneySense, MAS or CPF Board.", fontSize = 12.sp)
        }

        // =================================
        // SCREEN 8: INDIVIDUAL LESSON
        // =================================
        8 -> {
            val lesson = lessons[selectedLesson.coerceIn(lessons.indices)]
            val context = LocalContext.current
            Page {
                ScreenHeading(lesson.title, lesson.summary)
                Spacer(Modifier.height(24.dp))
                lesson.paragraphs.forEach { paragraph ->
                    Text(paragraph)
                    Spacer(Modifier.height(18.dp))
                }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Try This", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(lesson.tip)
                    }
                }
                Spacer(Modifier.height(16.dp))
                when (lesson.title) {
                    "Budgeting Basics" -> BudgetingExampleCard(
                        takeHomeIncome = monthlyIncome,
                        monthlyExpenses = monthlyExpenses,
                        plannedMonthlySavings = monthlySaving
                    )
                    "Emergency Funds" -> EmergencyFundExampleCard(
                        currentSavings = currentSavings,
                        monthlyExpenses = monthlyExpenses
                    )
                    "Needs vs Wants" -> NeedsWantsExampleCard()
                    "Credit Cards & Debt Management" -> DebtExampleCard()
                    "Investing Basics" -> InvestingExampleCard()
                    "Insurance Basics" -> InsuranceExampleCard()
                }
                Spacer(Modifier.height(24.dp))
                if (lesson.title == "Financial Scam Awareness") {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(18.dp)) {
                            Text("ScamShield Singapore", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("Check suspicious calls, messages and website links with ScamShield. The official helpline is 1799 (24/7).")
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.scamshield.gov.sg/")))
                                }, modifier = Modifier.fillMaxWidth()
                            ) { Text("Open ScamShield Website") }
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://go.gov.sg/ss-android")))
                                }, modifier = Modifier.fillMaxWidth()
                            ) { Text("Get ScamShield Android App") }
                            Spacer(Modifier.height(8.dp))
                            Text("ScamShield checks help identify suspicious content but cannot guarantee that something is safe. If you have transferred money, call your bank immediately; file a police report if scammed.", fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
                Text("Learn more from the official source:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(lesson.sourceUrl)))
                }, modifier = Modifier.fillMaxWidth()) { Text(lesson.sourceName) }
                Spacer(Modifier.height(8.dp))
                Text("Official information may change. Check the linked source for updates.", fontSize = 12.sp)
                Spacer(Modifier.height(24.dp))
                Button(onClick = { screen = 7 }, modifier = Modifier.fillMaxWidth()) { Text("Back to Learning Hub") }
            }
        }

        // =================================
        // SCREEN 9: QUICK FINANCIAL FLASHCARDS — NO GRADING
        9 -> Page {
            ScreenHeading("Financial Flashcards", "Quick financial lessons for busy fresh graduates.")
            Spacer(Modifier.height(16.dp))
            Text("Tap to reveal an answer, explanation and tip. No scores, no pressure.")
            Spacer(Modifier.height(18.dp))
            val card = quizQuestions[questionIndex]
            Text("Card ${questionIndex + 1} of ${quizQuestions.size} • ${quizTopics[questionIndex]}",
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { (questionIndex + 1).toFloat() / quizQuestions.size },
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            Card(
                onClick = { flashcardRevealed = !flashcardRevealed },
                modifier = Modifier.fillMaxWidth().heightIn(min = 270.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor =
                    if (flashcardRevealed) Color(0xFFE2F4EE) else Color(0xFFE7E7FA))
            ) {
                Column(Modifier.fillMaxWidth().padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Text(if (flashcardRevealed) "ANSWER & WHY" else "QUICK QUESTION",
                        color = if (flashcardRevealed) Color(0xFF216F5D) else Color(0xFF5650A6),
                        fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(18.dp))
                    if (!flashcardRevealed) {
                        Text(card.question, fontSize = 21.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF30334E))
                    } else {
                        Text(card.choices[card.correctIndex], fontWeight = FontWeight.Bold,
                            fontSize = 19.sp, color = Color(0xFF225B4E))
                        Spacer(Modifier.height(14.dp))
                        Text(card.explanation, fontSize = 14.sp, color = Color(0xFF253F38))
                        Spacer(Modifier.height(14.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        Text("FinanceBuddy Tip", fontWeight = FontWeight.Bold,
                            color = Color(0xFF216F5D))
                        Spacer(Modifier.height(6.dp))
                        Text(card.practicalTip, fontSize = 14.sp, color = Color(0xFF253F38))
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("Tap card to ${if (flashcardRevealed) "see question" else "reveal answer"}",
                        fontSize = 12.sp, color = Color(0xFF62667C))
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    questionIndex = (questionIndex - 1).coerceAtLeast(0)
                    flashcardRevealed = false
                }, enabled = questionIndex > 0, modifier = Modifier.weight(1f)) {
                    Text("Previous")
                }
                Button(onClick = {
                    questionIndex = (questionIndex + 1) % quizQuestions.size
                    flashcardRevealed = false
                }, modifier = Modifier.weight(1f)) {
                    Text(if (questionIndex == quizQuestions.lastIndex) "Start Again" else "Next Card")
                }
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = { screen = 4 }, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Dashboard")
            }
        }

        11 -> Page {
            ScreenHeading("CPF Calculator & Guide", "Understand employee and employer CPF contributions")
            Spacer(Modifier.height(18.dp))
            CPFContributionCalculator()
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                selectedLesson = lessons.indexOfFirst { it.title == "CPF Explained" }.coerceAtLeast(0)
                screen = 8
            }, modifier = Modifier.fillMaxWidth()) { Text("Learn More About CPF") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { screen = 4 }, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Dashboard")
            }
        }

        10 -> Page {
            ScreenHeading("Real-Life Scenarios", "Practise financial decisions in everyday situations.")
            Spacer(Modifier.height(12.dp))
            Text("Each scenario starts with your Dashboard figures. Change the amounts to explore decisions; nothing here changes your actual budget.")
            Spacer(Modifier.height(20.dp))
            listOf(
                "Your First Paycheque" to "Build a personalised salary plan",
                "Unexpected Expense" to "Decide how to handle a surprise cost",
                "Lifestyle Spending" to "Discover the impact of small spending cuts",
                "Suspicious Investment Offer" to "Decide how to respond safely"
            ).forEachIndexed { idx, item ->
                Card(Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(containerColor =
                        if (selectedScenario == idx) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerLow)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(item.first, fontWeight = FontWeight.Bold)
                        Text(item.second, fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { selectedScenario = idx },
                            modifier = Modifier.fillMaxWidth()) {
                            Text(if (selectedScenario == idx) "Selected" else "Explore Scenario")
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            when (selectedScenario) {
                0 -> FirstPaychequeScenario(monthlyIncome, monthlyExpenses, monthlySaving, targetAmount, allocatedAmount)
                1 -> EmergencyExpenseScenario(monthlyIncome, monthlyExpenses, currentSavings, monthlySaving)
                2 -> LifestyleScenario(monthlyIncome, monthlyExpenses, savedCategories, hasSavedBudget)
                else -> ScamOfferScenario(monthlyIncome, currentSavings)
            }
            Spacer(Modifier.height(20.dp))
            Button(onClick = { screen = 4 }, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Dashboard")
            }
        }
    }
}


// =======================================
// DASHBOARD VISUAL COMPONENTS (STEP 15 UI)
// =======================================
private val budgetColors = listOf(
    Color(0xFF5654D4), Color(0xFF17A5A4), Color(0xFFF2A64A),
    Color(0xFF9C6BCD), Color(0xFFDE7078), Color(0xFF527FC9),
    Color(0xFF9298A9)
)

// Prototype-only score based on users' reported budgets, not a credit score.
// Weights: spending coverage 35, savings affordability 25,
// positive savings habit 20, goal allocation 10, budget completion 10.
fun calculateHealthScore(
    income: Double,
    expenses: Double,
    plannedSavings: Double,
    allocatedSavings: Double,
    hasSavedBudget: Boolean
): Int {
    if (income <= 0.0) return 0
    val expenseRatio = expenses / income
    val freeBalance = income - expenses

    val expensePoints = when {
        expenseRatio <= 0.60 -> 35
        expenseRatio <= 0.80 -> 28
        expenseRatio <= 1.00 -> 18
        else -> 0
    }
    val affordablePoints = when {
        plannedSavings > 0.0 && plannedSavings <= freeBalance -> 25
        plannedSavings > 0.0 && freeBalance > 0.0 -> 10
        else -> 0
    }
    val savingsRatio = plannedSavings / income
    val savingsPoints = when {
        savingsRatio >= 0.20 -> 20
        savingsRatio >= 0.10 -> 15
        plannedSavings > 0.0 -> 10
        else -> 0
    }
    val allocationPoints = if (allocatedSavings > 0.0) 10 else 0
    val budgetPoints = if (hasSavedBudget) 10 else 0
    return (expensePoints + affordablePoints + savingsPoints +
            allocationPoints + budgetPoints).coerceIn(0, 100)
}

fun healthLabel(score: Int): String = when {
    score < 40 -> "Poor"
    score < 60 -> "Normal"
    score < 80 -> "Good"
    else -> "Excellent"
}

@Composable
fun FinancialHealthPanel(
    income: Double,
    expenses: Double,
    plannedSavings: Double,
    allocatedSavings: Double,
    hasSavedBudget: Boolean
) {
    val score = calculateHealthScore(
        income, expenses, plannedSavings, allocatedSavings, hasSavedBudget
    )
    val tips = buildList {
        if (income <= 0.0) {
            add("Enter your take-home income")
        } else if (expenses > income) {
            add("Review spending above your income")
        } else {
            add("Your income covers your expenses")
        }
        if (plannedSavings <= 0.0) {
            add("Set a monthly savings contribution")
        } else if (plannedSavings > income - expenses) {
            add("Adjust savings to fit your budget")
        } else {
            add("Your planned savings fit your budget")
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF514DC8)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Financial Health", color = Color.White.copy(alpha = 0.88f),
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(healthLabel(score), color = Color.White,
                    fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                tips.forEach { tip ->
                    Text("• $tip", color = Color.White.copy(alpha = 0.95f),
                        fontSize = 12.sp, lineHeight = 17.sp)
                    Spacer(Modifier.height(7.dp))
                }
            }
            Box(modifier = Modifier.size(104.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier.size(98.dp),
                    strokeWidth = 8.dp,
                    color = Color(0xFF2EE6A6),
                    trackColor = Color.White.copy(alpha = 0.23f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$score", color = Color.White, fontSize = 28.sp,
                        fontWeight = FontWeight.Bold)
                    Text("/100", color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun MonthlyOverviewPanel(income: Double, expenses: Double, plannedSavings: Double) {
    val balance = income - expenses
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(18.dp)) {
            Text("Monthly Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OverviewMetric("Income", income, Modifier.weight(1f))
                OverviewMetric("Spent", expenses, Modifier.weight(1f))
                OverviewMetric("Planned to Save", plannedSavings, Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Remaining monthly balance", fontSize = 13.sp)
                Text(formatSGD(balance), fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = if (balance < 0) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(7.dp))
            Text("After planned savings: ${formatSGD(balance - plannedSavings)}",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Planned savings are part of your balance, not an extra expense.",
                fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun OverviewMetric(label: String, amount: Double, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, maxLines = 2,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text(formatCompactSGD(amount), fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun BudgetBreakdownPanel(
    hasSavedBudget: Boolean,
    savedCategories: List<String>,
    totalExpenses: Double,
    onOpenBudget: () -> Unit
) {
    val values = savedCategories.map { it.toDoubleOrNull() ?: 0.0 }
    val sum = values.sum()
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(18.dp)) {
            Text("Budget Breakdown", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            if (!hasSavedBudget) {
                Text("Categorise your ${formatSGD(totalExpenses)} monthly expenses to see a chart here.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onOpenBudget, modifier = Modifier.fillMaxWidth()) {
                    Text("Open Budget Tracker")
                }
            } else if (sum <= 0.0) {
                Text("No spending categories recorded yet.", fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onOpenBudget, modifier = Modifier.fillMaxWidth()) {
                    Text("Edit Budget Categories")
                }
            } else {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(128.dp), contentAlignment = Alignment.Center) {
                        Canvas(Modifier.fillMaxSize()) {
                            val stroke = 24.dp.toPx()
                            var start = -90f
                            val diameter = size.minDimension - stroke
                            values.forEachIndexed { idx, value ->
                                if (value > 0) {
                                    val sweep = (value / sum * 360f).toFloat()
                                    drawArc(budgetColors[idx], start, sweep,
                                        useCenter = false,
                                        topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
                                        size = Size(diameter, diameter),
                                        style = Stroke(width = stroke, cap = StrokeCap.Butt))
                                    start += sweep
                                }
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Spent", fontSize = 11.sp)
                            Text(formatCompactSGD(sum), fontSize = 15.sp,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        values.forEachIndexed { index, value ->
                            if (value > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 3.dp)) {
                                    Box(Modifier.size(9.dp).background(budgetColors[index], RoundedCornerShape(2.dp)))
                                    Spacer(Modifier.width(6.dp))
                                    Text(categoryNames[index], fontSize = 10.sp,
                                        modifier = Modifier.weight(1f), maxLines = 2)
                                    Text("${(value / sum * 100).toInt()}%", fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(9.dp))
                Text("Total categorised: ${formatSGD(sum)}", fontSize = 13.sp,
                    fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onOpenBudget, modifier = Modifier.fillMaxWidth()) {
                    Text("View / Edit Budget")
                }
            }
        }
    }
}

private fun formatCompactSGD(amount: Double): String =
    if (kotlin.math.abs(amount) >= 1000.0) {
        "S$" + String.format(Locale.US, "%,.0f", amount)
    } else {
        "S$" + String.format(Locale.US, "%,.0f", amount)
    }

// =======================================
// PERSONALIZED FINANCIAL LEARNING CARDS
// =======================================
@Composable
fun BudgetingExampleCard(
    takeHomeIncome: Double,
    monthlyExpenses: Double,
    plannedMonthlySavings: Double
) {
    val suggestedSavings = takeHomeIncome * 0.20
    val balance = takeHomeIncome - monthlyExpenses
    val afterSuggestedSaving = balance - suggestedSavings
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Try This — Your Savings Estimate", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Your monthly take-home income: ${formatSGD(takeHomeIncome)}")
            Text("Your entered monthly expenses: ${formatSGD(monthlyExpenses)}")
            Spacer(Modifier.height(10.dp))
            Text("Illustrative 20% savings guideline: ${formatSGD(suggestedSavings)}", fontWeight = FontWeight.Bold)
            Text("Your current planned savings: ${formatSGD(plannedMonthlySavings)}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("Estimated balance after expenses and 20% savings:")
            Text(
                formatSGD(afterSuggestedSaving),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (afterSuggestedSaving < 0) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text("At this rate, the suggested savings would total ${formatSGD(suggestedSavings * 12)} in 12 months, excluding interest.")
            if (afterSuggestedSaving < 0) {
                Spacer(Modifier.height(8.dp))
                Text("Your current expenses may not leave enough for a 20% savings target. Start with an achievable amount and review your budget.", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))
            Text("20% is a general planning guideline, not a required amount. This estimate does not automatically change your budget.", fontSize = 12.sp)
        }
    }
}

@Composable
fun EmergencyFundExampleCard(currentSavings: Double, monthlyExpenses: Double) {
    val threeTarget = monthlyExpenses * 3
    val sixTarget = monthlyExpenses * 6
    val coverage = if (monthlyExpenses > 0) currentSavings / monthlyExpenses else null
    val reachedThree = coverage != null && coverage >= 3
    val reachedSix = coverage != null && coverage >= 6
    val statusColor = when {
        coverage == null -> Color(0xFF68718B)
        coverage < 3 -> Color(0xFFB45309)
        coverage < 6 -> Color(0xFF8A681B)
        else -> Color(0xFF087F69)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FD)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("Try This — Your Emergency Fund", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("An emergency fund helps cover necessary expenses if your income stops or an unexpected cost occurs.", fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            Text("Current total savings: ${formatSGD(currentSavings)}")
            Text("Monthly expenses: ${formatSGD(monthlyExpenses)}")
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Your savings could cover your current expenses for approximately", fontSize = 13.sp)
                    Spacer(Modifier.height(7.dp))
                    Text(
                        if (coverage == null) "Expenses not set" else String.format(Locale.US, "%.1f months", coverage),
                        fontSize = 28.sp, fontWeight = FontWeight.Bold, color = statusColor
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(when {
                        coverage == null -> "Enter monthly expenses in Budget Tracker to calculate coverage."
                        coverage < 3 -> "Below the common 3-month starting guideline."
                        coverage < 6 -> "You meet the 3-month target; consider whether a 6-month buffer is appropriate."
                        else -> "Your total savings exceed the common 6-month expense guideline."
                    }, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(15.dp))
            Text("Emergency savings milestones", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            EmergencyMilestone("3-month target", threeTarget, reachedThree)
            Spacer(Modifier.height(9.dp))
            EmergencyMilestone("6-month target", sixTarget, reachedSix)
            if (monthlyExpenses > 0) {
                Spacer(Modifier.height(12.dp))
                Text("Still needed for 3 months: ${formatSGD((threeTarget - currentSavings).coerceAtLeast(0.0))}", fontSize = 13.sp)
                Text("Still needed for 6 months: ${formatSGD((sixTarget - currentSavings).coerceAtLeast(0.0))}", fontSize = 13.sp)
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "Important: This assumes all your current savings are accessible for emergencies. It uses total monthly expenses, not just essentials. Savings allocated to other goals may not be available, so actual coverage could be lower.",
                fontSize = 12.sp, color = Color(0xFF52596C), lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun EmergencyMilestone(label: String, amount: Double, reached: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Medium)
            Text(formatSGD(amount), fontSize = 13.sp)
        }
        Text(if (reached) "✓ Reached" else "Not reached",
            color = if (reached) Color(0xFF087F69) else Color(0xFFB45309),
            fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NeedsWantsExampleCard() {
    val examples = listOf(
        Triple("Basic groceries", "Need", "Food for daily living"),
        Triple("Public transport to work", "Need", "Travel required for employment"),
        Triple("Rent / essential utilities", "Need", "Housing and basic services"),
        Triple("Latest phone upgrade", "Want", "If your existing phone still works"),
        Triple("Streaming subscriptions", "Want", "Optional entertainment"),
        Triple("Cafe drinks / frequent delivery", "Want", "Usually optional convenience"),
        Triple("Holiday trip", "Want", "Optional leisure spending")
    )
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Try This — Needs vs Wants", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Common spending decisions for fresh graduates in Singapore:", fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth()) {
                Text("Purchase", Modifier.weight(1.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Type", Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Reason", Modifier.weight(1.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            examples.forEach { (purchase, kind, reason) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
                    Text(purchase, Modifier.weight(1.6f), fontSize = 12.sp)
                    Text(kind, Modifier.weight(0.7f), fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (kind == "Need") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary)
                    Text(reason, Modifier.weight(1.6f), fontSize = 12.sp)
                }
                HorizontalDivider()
            }
            Spacer(Modifier.height(12.dp))
            Text("Remember: a purchase can change categories depending on your situation. A phone needed for work may be essential; an optional upgrade may not be.", fontSize = 12.sp)
        }
    }
}

@Composable
fun Page(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        content()
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun ScreenHeading(title: String, subtitle: String) {
    Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Text(subtitle)
}

@Composable
fun RadioChoices(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    options.forEach { option ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected == option, onClick = { onSelect(option) })
            Text(option)
        }
    }
}

@Composable
fun FinanceInputField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 12 && newValue.matches(Regex("""\d*(\.\d{0,2})?"""))) {
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CategoryInput(title: String, value: String, onValueChange: (String) -> Unit) {
    Text(title, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    FinanceInputField(value, onValueChange, "Amount (S$)")
    Spacer(Modifier.height(20.dp))
}

@Composable
fun SummaryCard(title: String, value: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun SpendingBar(category: String, amount: Double, total: Double) {
    val fraction = if (total > 0) (amount / total).toFloat().coerceIn(0f, 1f) else 0f
    Column(Modifier.padding(bottom = 16.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(category, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text(formatSGD(amount), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun SavingsProgressCard(
    goal: String, allocatedSavings: Double, targetAmount: Double,
    progress: Float, amountRemaining: Double, monthsNeeded: Int?
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text("Your Savings Progress", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            Text("Goal: $goal")
            Text("Allocated to This Goal: ${formatSGD(allocatedSavings)}")
            Text("Target Savings: ${formatSGD(targetAmount)}")
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text(String.format(Locale.US, "%.1f%% completed", progress * 100))
            Spacer(Modifier.height(16.dp))
            Text("Amount Remaining: ${formatSGD(amountRemaining)}")
            Spacer(Modifier.height(12.dp))
            Text(when {
                monthsNeeded == 0 -> "Congratulations! Your target is reached."
                monthsNeeded != null -> "Estimated Time: $monthsNeeded months"
                else -> "Enter monthly savings above zero to estimate the time needed."
            }, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Progress uses only savings allocated to this goal. The time estimate assumes your planned monthly savings go toward this goal, with no interest or withdrawals.", fontSize = 12.sp)
        }
    }
}

@Composable
fun ProjectionBar(title: String, amount: Double, maximum: Double) {
    val fraction = if (maximum > 0) (amount / maximum).toFloat().coerceIn(0f, 1f) else 0f
    Column(Modifier.padding(bottom = 20.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(title, modifier = Modifier.weight(1f))
            Text(formatSGD(amount), fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth().height(12.dp))
    }
}

fun monthsToTarget(amountRemaining: Double, monthlySaving: Double): Int? {
    if (amountRemaining <= 0) return 0
    if (monthlySaving <= 0) return null
    val months = ceil(amountRemaining / monthlySaving)
    return if (months.isFinite() && months <= Int.MAX_VALUE) months.toInt() else null
}

fun isValidMoney(value: String): Boolean {
    val amount = value.toDoubleOrNull()
    return amount != null && amount.isFinite() && amount >= 0
}

fun formatSGD(amount: Double): String = "S$" + String.format(Locale.US, "%,.2f", amount)
fun moneyInput(amount: Double): String = String.format(Locale.US, "%.2f", amount)


// =======================================
// STEP 16: NEW FINANCIAL LITERACY EXAMPLES
// =======================================
@Composable
fun DebtExampleCard() {
    var balanceText by rememberSaveable { mutableStateOf("1000") }
    var rateText by rememberSaveable { mutableStateOf("24") }
    val balance = balanceText.toDoubleOrNull() ?: 0.0
    val annualRate = rateText.toDoubleOrNull() ?: 0.0
    val valid = isValidMoney(balanceText) && isValidMoney(rateText) && annualRate <= 100.0
    val afterYear = balance * (1 + annualRate / 1200.0).let { monthly ->
        Math.pow(monthly, 12.0)
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Try This — Credit Card Debt", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("What if a balance remains unpaid for a year?", fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            FinanceInputField(balanceText, { balanceText = it }, "Unpaid balance (S$)")
            Spacer(Modifier.height(10.dp))
            FinanceInputField(rateText, { rateText = it }, "Illustrative annual interest rate (%)")
            Spacer(Modifier.height(12.dp))
            if (valid) {
                Text("Starting debt: ${formatSGD(balance)}")
                Text("After 12 months: ${formatSGD(afterYear)}", fontWeight = FontWeight.Bold)
                Text("Illustrative interest added: ${formatSGD(afterYear - balance)}")
            } else {
                Text("Enter a valid non-negative balance and a rate from 0% to 100%.", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(10.dp))
            Text("Assumes monthly compounding, no payments, no new spending and no fees. Actual bank calculations, daily interest, grace periods and charges differ. This is not a repayment quotation.", fontSize = 12.sp)
        }
    }
}

@Composable
fun InvestingExampleCard() {
    var monthlyText by rememberSaveable { mutableStateOf("100") }
    var rateText by rememberSaveable { mutableStateOf("4") }
    var years by rememberSaveable { mutableIntStateOf(5) }
    val monthly = monthlyText.toDoubleOrNull() ?: 0.0
    val annualRate = rateText.toDoubleOrNull() ?: 0.0
    val valid = isValidMoney(monthlyText) && rateText.toDoubleOrNull() != null &&
            annualRate.isFinite() && annualRate in -20.0..20.0
    val months = years * 12
    val monthlyRate = annualRate / 1200.0
    val projected = if (kotlin.math.abs(monthlyRate) < 0.0000001) monthly * months
    else monthly * ((Math.pow(1.0 + monthlyRate, months.toDouble()) - 1.0) / monthlyRate)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Try This — Investing Illustration", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Compare monthly contributions and a hypothetical return.", fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            FinanceInputField(monthlyText, { monthlyText = it }, "Monthly investment (S$)")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = rateText,
                onValueChange = { v -> if (v.length <= 6 && v.matches(Regex("""-?\d*(\.\d{0,2})?"""))) rateText = v },
                label = { Text("Hypothetical annual return (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 10, 20).forEach { value ->
                    FilterChip(selected = years == value, onClick = { years = value }, label = { Text("$value yrs") })
                }
            }
            Spacer(Modifier.height(12.dp))
            if (valid) {
                Text("Total invested: ${formatSGD(monthly * months)}")
                Text("Illustrative future value: ${formatSGD(projected)}", fontWeight = FontWeight.Bold)
                Text("Illustrative gain/loss: ${formatSGD(projected - monthly * months)}")
            } else {
                Text("Enter a monthly amount and an assumed rate between -20% and +20%.", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(10.dp))
            Text("Assumes end-of-month contributions and a constant annual return converted to monthly compounding; excludes fees, taxes and inflation. Real investment returns fluctuate and losses are possible.", fontSize = 12.sp)
        }
    }
}

@Composable
fun InsuranceExampleCard() {
    var billText by rememberSaveable { mutableStateOf("5000") }
    var deductibleText by rememberSaveable { mutableStateOf("1000") }
    var copayText by rememberSaveable { mutableStateOf("10") }
    val bill = billText.toDoubleOrNull() ?: 0.0
    val deductible = deductibleText.toDoubleOrNull() ?: 0.0
    val copay = copayText.toDoubleOrNull() ?: 0.0
    val valid = listOf(billText, deductibleText, copayText).all(::isValidMoney) && copay <= 100.0
    val deductiblePaid = minOf(bill, deductible)
    val coPaid = (bill - deductiblePaid) * copay / 100
    val ownCost = deductiblePaid + coPaid
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Try This — Insurance Costs", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Illustrative medical bill with a deductible and co-payment.", fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            FinanceInputField(billText, { billText = it }, "Eligible medical bill (S$)")
            Spacer(Modifier.height(10.dp))
            FinanceInputField(deductibleText, { deductibleText = it }, "Deductible (S$)")
            Spacer(Modifier.height(10.dp))
            FinanceInputField(copayText, { copayText = it }, "Co-payment after deductible (%)")
            Spacer(Modifier.height(12.dp))
            if (valid) {
                Text("Illustrative amount you pay: ${formatSGD(ownCost)}", fontWeight = FontWeight.Bold)
                Text("Illustrative insurer portion: ${formatSGD(bill - ownCost)}")
            } else {
                Text("Enter valid amounts and co-payment between 0% and 100%.", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(10.dp))
            Text("Simplified teaching example only. Real cover may involve exclusions, limits, panel rules, claim conditions and other charges. This is not a policy benefit calculation.", fontSize = 12.sp)
        }
    }
}


// =======================================
// DASHBOARD NAVIGATION TILES
// =======================================
@Composable
private fun DashboardTile(title: String, subtitle: String, modifier: Modifier = Modifier,
                          onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier.height(118.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = when (title) {
            "Budget Tracker" -> Color(0xFFE5EEFF)
            "Savings Simulator" -> Color(0xFFEEE7FF)
            "Financial Goals" -> Color(0xFFDEF6EF)
            "CPF Calculator" -> Color(0xFFE3E9FA)
            else -> MaterialTheme.colorScheme.surfaceContainerLow
        })) {
        Column(Modifier.fillMaxSize().padding(13.dp), verticalArrangement = Arrangement.Center) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(7.dp))
            Text(subtitle, fontSize = 12.sp, lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DashboardLink(title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = when (title) {
        "Financial Learning Hub" -> Color(0xFFEAF0FF)
        "Financial Literacy Quiz" -> Color(0xFFF1EAFE)
        "Real-Life Scenarios" -> Color(0xFFE4F5F2)
        else -> Color.Transparent
    }),
        modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 13.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

// =======================================
// INTERACTIVE REAL-LIFE SCENARIOS
// =======================================
// =======================================
// PERSONALISED SCENARIO COMPONENTS
// =======================================
@Composable
private fun ScenarioModePicker() {
    Text("Starting values loaded from your Dashboard. Edit any amount to explore a what-if scenario.",
        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun ScenarioAmount(title: String, value: String, change: (String) -> Unit) {
    Text(title, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(5.dp))
    FinanceInputField(value, change, "Amount (S$)")
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun ScenarioFeedback(title: String, message: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message)
        }
    }
}

@Composable
private fun FirstPaychequeScenario(
    dashboardIncome: Double,
    dashboardExpenses: Double,
    dashboardMonthlySavings: Double,
    goalTarget: Double,
    goalAllocated: Double
) {
    var salary by rememberSaveable { mutableStateOf(moneyInput(dashboardIncome)) }
    var essentials by rememberSaveable { mutableStateOf(moneyInput(dashboardExpenses)) }
    var contribution by rememberSaveable { mutableStateOf(moneyInput(dashboardMonthlySavings)) }
    var purchaseCost by rememberSaveable { mutableStateOf("250") }
    var choice by rememberSaveable { mutableIntStateOf(-1) }
    fun reset() {
        salary = moneyInput(dashboardIncome)
        essentials = moneyInput(dashboardExpenses)
        contribution = moneyInput(dashboardMonthlySavings)
        purchaseCost = "250"
        choice = -1
    }
    val valid = listOf(salary, essentials, contribution, purchaseCost).all(::isValidMoney)
    val income = salary.toDoubleOrNull() ?: 0.0
    val expense = essentials.toDoubleOrNull() ?: 0.0
    val monthly = contribution.toDoubleOrNull() ?: 0.0
    val cost = purchaseCost.toDoubleOrNull() ?: 0.0
    val spare = income - expense - monthly
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Your First Paycheque — Decision Challenge", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Choose how you would handle an optional purchase. To compare its effect on your savings goal, assume you redirect money that would otherwise go towards that goal.")
            Spacer(Modifier.height(12.dp))
            ScenarioModePicker()
            Spacer(Modifier.height(12.dp))
            ScenarioAmount("Monthly take-home income", salary) { salary = it }
            ScenarioAmount("Essential monthly expenses", essentials) { essentials = it }
            ScenarioAmount("Monthly savings towards your goal", contribution) { contribution = it }
            ScenarioAmount("Cost of optional purchase", purchaseCost) { purchaseCost = it }
            if (valid) {
                Text("Cash left after essentials and planned savings: ${formatSGD(spare)}",
                    fontWeight = FontWeight.Bold,
                    color = if (spare < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(12.dp))
                Text("What would you choose?", fontWeight = FontWeight.Bold)
                listOf("Buy it using money meant for my savings goal",
                    "Skip it and add the price to my savings goal",
                    "Reserve half the price from goal savings for a later purchase").forEachIndexed { index, label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = choice == index, onClick = { choice = index })
                        Text(label, Modifier.weight(1f), fontSize = 13.sp)
                    }
                }
                if (choice >= 0) {
                    Spacer(Modifier.height(12.dp))
                    val baseline = monthsToTarget((goalTarget - goalAllocated).coerceAtLeast(0.0), monthly)
                    val adjustedAllocation = when (choice) {
                        0 -> (goalAllocated - cost).coerceAtLeast(0.0)
                        1 -> goalAllocated + cost
                        else -> (goalAllocated - cost / 2).coerceAtLeast(0.0)
                    }
                    val adjusted = monthsToTarget((goalTarget - adjustedAllocation).coerceAtLeast(0.0), monthly)
                    val difference = if (baseline != null && adjusted != null) adjusted - baseline else null
                    val explanation = when (choice) {
                        0 -> "Buying the item redirects ${formatSGD(cost)} away from your goal. This assumes that amount would otherwise be available for the goal."
                        1 -> "Skipping the purchase and actively assigning ${formatSGD(cost)} to your goal increases the amount allocated to it."
                        else -> "Reserving ${formatSGD(cost / 2)} for a later purchase means that amount is no longer available for your goal."
                    }
                    ScenarioFeedback("Impact on your savings goal", buildString {
                        append(explanation)
                        append("\n\n")
                        if (goalTarget <= 0 || monthly <= 0 || baseline == null || adjusted == null) {
                            append("Set a positive goal target and monthly contribution to estimate the time impact.")
                        } else {
                            append("Original estimate: $baseline months. New estimate: $adjusted months. ")
                            append(when {
                                difference == null -> ""
                                difference > 0 -> "This could delay your goal by $difference month(s)."
                                difference < 0 -> "This could bring the goal forward by ${-difference} month(s)."
                                else -> "The rounded target month is unchanged, although your allocated savings differ."
                            })
                        }
                        append("\nThis is a what-if calculation, not a change to your saved goal.")
                    })
                    if (spare >= cost && choice == 0) {
                        Text("Alternative: If you pay entirely from unrelated spare cash, your goal may not be delayed.", fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { reset() }, modifier = Modifier.fillMaxWidth()) { Text("Reset to Dashboard Values") }
        }
    }
}

@Composable
private fun EmergencyExpenseScenario(
    dashboardIncome: Double,
    dashboardExpenses: Double,
    dashboardSavings: Double,
    dashboardMonthlySavings: Double
) {
    var cost by rememberSaveable { mutableStateOf("800") }
    var available by rememberSaveable { mutableStateOf(moneyInput(dashboardSavings)) }
    var salary by rememberSaveable { mutableStateOf(moneyInput(dashboardIncome)) }
    var expenses by rememberSaveable { mutableStateOf(moneyInput(dashboardExpenses)) }
    var planned by rememberSaveable { mutableStateOf(moneyInput(dashboardMonthlySavings)) }
    var choice by rememberSaveable { mutableIntStateOf(-1) }
    fun reset() {
        cost = "800"
        available = moneyInput(dashboardSavings)
        salary = moneyInput(dashboardIncome)
        expenses = moneyInput(dashboardExpenses)
        planned = moneyInput(dashboardMonthlySavings)
        choice = -1
    }
    val valid = listOf(cost, available, salary, expenses, planned).all(::isValidMoney)
    val repair = cost.toDoubleOrNull() ?: 0.0
    val emergencyMoney = available.toDoubleOrNull() ?: 0.0
    val spare = (salary.toDoubleOrNull() ?: 0.0) -
            (expenses.toDoubleOrNull() ?: 0.0) - (planned.toDoubleOrNull() ?: 0.0)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Unexpected Expense", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("An essential item suddenly breaks. Explore how to pay without forgetting your other bills.")
            Spacer(Modifier.height(12.dp))
            ScenarioModePicker()
            Spacer(Modifier.height(12.dp))
            ScenarioAmount("Unexpected repair cost", cost) { cost = it }
            ScenarioAmount("Accessible emergency savings", available) { available = it }
            ScenarioAmount("Monthly take-home income", salary) { salary = it }
            ScenarioAmount("Monthly expenses", expenses) { expenses = it }
            ScenarioAmount("Planned monthly savings", planned) { planned = it }
            if (valid) {
                Text("Remaining monthly cash after bills and planned savings: ${formatSGD(spare)}",
                    fontWeight = FontWeight.SemiBold,
                    color = if (spare < repair) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(14.dp))
            Text("How would you respond?", fontWeight = FontWeight.Bold)
            listOf("Use remaining monthly income", "Use accessible emergency savings",
                "Borrow using a credit card", "Reduce or delay optional spending").forEachIndexed { index, option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = choice == index, onClick = { choice = index })
                    Text(option, Modifier.weight(1f), fontSize = 13.sp)
                }
            }
            if (valid && choice >= 0) {
                Spacer(Modifier.height(12.dp))
                val result = when (choice) {
                    0 -> if (spare >= repair) "Your remaining monthly cash covers the repair. You would have ${formatSGD(spare - repair)} left after the repair, without touching planned savings." else "Your remaining monthly cash is short by ${formatSGD(repair - spare)}. Consider adjusting flexible spending or savings contributions, or using an accessible emergency fund."
                    1 -> if (emergencyMoney >= repair) "Your accessible emergency fund covers the repair and would decrease to ${formatSGD(emergencyMoney - repair)}. Plan to replenish what you use." else "Your available emergency savings fall short by ${formatSGD(repair - emergencyMoney)}. You may need to combine resources or reconsider the timing."
                    2 -> "Charging ${formatSGD(repair)} to a credit card creates a repayment obligation. Paying only the minimum may cause interest to accumulate. Check the repayment date and terms."
                    else -> if (spare >= repair) "Your existing spare monthly cash could cover the repair already; reducing optional purchases would give you extra breathing room." else "You would need to free up at least ${formatSGD(repair - spare)} through optional spending cuts or other adjustments. Do not assume all expenses are flexible."
                }
                ScenarioFeedback("Decision outcome", result)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { reset() }, modifier = Modifier.fillMaxWidth()) { Text("Reset to Dashboard Values") }
        }
    }
}

@Composable
private fun LifestyleScenario(
    dashboardIncome: Double,
    dashboardExpenses: Double,
    savedCategories: List<String>,
    hasSavedBudget: Boolean
) {
    val shoppingPrefill = if (hasSavedBudget) savedCategories.getOrNull(4)?.toDoubleOrNull() ?: 0.0 else 0.0
    val entertainmentPrefill = if (hasSavedBudget) savedCategories.getOrNull(5)?.toDoubleOrNull() ?: 0.0 else 0.0
    var shopping by rememberSaveable { mutableStateOf(moneyInput(shoppingPrefill)) }
    var entertainment by rememberSaveable { mutableStateOf(moneyInput(entertainmentPrefill)) }
    var cafes by rememberSaveable { mutableStateOf("0") }
    var subscriptions by rememberSaveable { mutableStateOf("0") }
    var cutPercent by rememberSaveable { mutableFloatStateOf(25f) }
    fun reset() {
        shopping = moneyInput(shoppingPrefill)
        entertainment = moneyInput(entertainmentPrefill)
        cafes = "0"
        subscriptions = "0"
        cutPercent = 25f
    }
    val valid = listOf(shopping, entertainment, cafes, subscriptions).all(::isValidMoney)
    val shoppingValue = shopping.toDoubleOrNull() ?: 0.0
    val entertainmentValue = entertainment.toDoubleOrNull() ?: 0.0
    val cafesValue = cafes.toDoubleOrNull() ?: 0.0
    val subValue = subscriptions.toDoubleOrNull() ?: 0.0
    val optional = shoppingValue + entertainmentValue + cafesValue + subValue
    val reduction = optional * cutPercent / 100.0
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Lifestyle Spending — Habit Challenge", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Identify discretionary habits you could change. Shopping and Entertainment are prefilled from your Budget Tracker. Only enter café or subscription costs if they are NOT already counted there.")
            Spacer(Modifier.height(12.dp))
            ScenarioModePicker()
            Spacer(Modifier.height(12.dp))
            ScenarioAmount("Shopping (per month)", shopping) { shopping = it }
            ScenarioAmount("Entertainment (per month)", entertainment) { entertainment = it }
            ScenarioAmount("Café / delivery costs not already counted", cafes) { cafes = it }
            ScenarioAmount("Subscriptions not already counted", subscriptions) { subscriptions = it }
            Text("Reduce these habits by ${cutPercent.toInt()}%", fontWeight = FontWeight.Medium)
            Slider(value = cutPercent, onValueChange = { cutPercent = it }, valueRange = 0f..100f, steps = 19)
            if (valid) {
                Spacer(Modifier.height(8.dp))
                Text("Total optional spending: ${formatSGD(optional)}", fontWeight = FontWeight.Bold)
                Text("Illustrative monthly spending cut: ${formatSGD(reduction)}")
                val overlapWarning = optional > dashboardExpenses + 0.005
                if (overlapWarning) {
                    Text("These optional costs exceed your saved monthly expenses. Check for overlap or update your Budget Tracker.",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(12.dp))
                ScenarioFeedback("Your habit-change outcome",
                    "If you stick to this reduction, you could free up ${formatSGD(reduction)} each month, or ${formatSGD(reduction * 12)} over 12 months. " +
                            "Which habit would you reduce first? Start with one realistic change. This is not a forecast or an automatic budget update.")
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { reset() }, modifier = Modifier.fillMaxWidth()) { Text("Reset to Dashboard Values") }
        }
    }
}

@Composable
private fun ScamOfferScenario(dashboardIncome: Double, dashboardSavings: Double) {
    var requestedAmount by rememberSaveable { mutableStateOf("1000") }
    var income by rememberSaveable { mutableStateOf(moneyInput(dashboardIncome)) }
    var savings by rememberSaveable { mutableStateOf(moneyInput(dashboardSavings)) }
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    val context = LocalContext.current
    fun reset() {
        requestedAmount = "1000"
        income = moneyInput(dashboardIncome)
        savings = moneyInput(dashboardSavings)
        selected = -1
    }
    val valid = listOf(requestedAmount, income, savings).all(::isValidMoney)
    val amount = requestedAmount.toDoubleOrNull() ?: 0.0
    val savingsAmount = savings.toDoubleOrNull() ?: 0.0
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Suspicious Investment Offer", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("A stranger promises guaranteed high returns with no risk and demands an urgent transfer. How would you respond?")
            Spacer(Modifier.height(12.dp))
            ScenarioModePicker()
            Spacer(Modifier.height(12.dp))
            ScenarioAmount("Amount requested by the stranger", requestedAmount) { requestedAmount = it }
            ScenarioAmount("Monthly take-home income", income) { income = it }
            ScenarioAmount("Current savings", savings) { savings = it }
            listOf("Transfer the requested amount immediately",
                "Stop and verify independently through official channels",
                "Share my banking credentials to confirm identity").forEachIndexed { i, option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected == i, onClick = { selected = i })
                    Text(option, Modifier.weight(1f), fontSize = 13.sp)
                }
            }
            if (valid && selected >= 0) {
                Spacer(Modifier.height(12.dp))
                val relative = if (savingsAmount > 0) String.format(Locale.US, "%.1f", amount / savingsAmount * 100) + "% of your savings. " else ""
                val message = when (selected) {
                    0 -> "Transferring ${formatSGD(amount)} could mean losing the entire amount if this is a scam. $relative Recovery may be difficult or impossible. Do not send a test payment."
                    1 -> "You avoided an immediate transfer. Verify the institution and representative independently using official MAS resources and check suspicious messages with ScamShield. These checks reduce risk but do not guarantee any investment is safe."
                    else -> "Sharing your password or OTP could allow criminals to access your accounts and cause losses beyond the ${formatSGD(amount)} originally requested. Never share login credentials or OTPs."
                }
                ScenarioFeedback(if (selected == 1) "Safer choice" else "High-risk choice", message)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.scamshield.gov.sg/")))
            }, modifier = Modifier.fillMaxWidth()) { Text("Open ScamShield Website") }
            Text("If money was transferred or login details were compromised, contact your bank immediately. Report scams to the police.", fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { reset() }, modifier = Modifier.fillMaxWidth()) { Text("Reset Scenario") }
        }
    }
}

// ---------------------------------------
// CPF Calculator & Guide (2026 illustration)
// ---------------------------------------
@Composable
private fun CPFContributionCalculator() {
    val context = LocalContext.current
    var grossText by rememberSaveable { mutableStateOf("3000") }
    var citizenship by rememberSaveable { mutableStateOf("Citizen") }
    var age by rememberSaveable { mutableStateOf("55 and below") }
    val ageOptions = listOf("55 and below", "Above 55 to 60", "Above 60 to 65", "Above 65 to 70", "Above 70")
    val employeeRates = listOf(0.20, 0.18, 0.125, 0.075, 0.05)
    val employerRates = listOf(0.17, 0.16, 0.125, 0.09, 0.075)
    val gross = grossText.toDoubleOrNull() ?: 0.0
    val valid = isValidMoney(grossText)
    val supported = citizenship == "Citizen" && gross > 750.0 && gross <= 8000.0
    val idx = ageOptions.indexOf(age).coerceAtLeast(0)
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(18.dp)) {
            Text("Try This — CPF Contributions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Use GROSS ordinary monthly wages, not your Dashboard take-home income.")
            Spacer(Modifier.height(16.dp))
            ScenarioAmount("Gross monthly ordinary wages", grossText) { grossText = it }
            Text("Residency status", fontWeight = FontWeight.Bold)
            listOf("Citizen", "Permanent Resident").forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = citizenship == option, onClick = { citizenship = option })
                    Text(if (option == "Citizen") "Singapore Citizen" else "Singapore Permanent Resident")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Age group", fontWeight = FontWeight.Bold)
            ageOptions.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = age == option, onClick = { age = option })
                    Text(option, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            if (!valid) {
                Text("Enter a valid, non-negative salary.", color = MaterialTheme.colorScheme.error)
            } else if (!supported) {
                Text("This simplified calculator covers Singapore Citizens earning more than S$750 and up to S$8,000 in ordinary monthly wages. For PRs, lower wages or additional wages, consult CPF Board's calculator; their contribution rules can differ.")
            } else {
                val employee = gross * employeeRates[idx]
                val employer = gross * employerRates[idx]
                SummaryCard("Estimated take-home pay before other deductions", formatSGD(gross - employee)) {
                    Text("Employee CPF (${String.format(Locale.US, "%.1f", employeeRates[idx] * 100)}%): ${formatSGD(employee)}")
                    Text("Employer CPF (${String.format(Locale.US, "%.1f", employerRates[idx] * 100)}%): ${formatSGD(employer)}")
                    Text("Total CPF: ${formatSGD(employee + employer)}", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("2026 educational approximation; actual CPF contributions use official calculation and rounding rules. The employer share is not deducted from the employee's gross pay. Rates and rules may change.", fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.cpf.gov.sg/employer/employer-obligations/how-much-cpf-contributions-to-pay")))
            }, modifier = Modifier.fillMaxWidth()) { Text("Official CPF Rates and Calculator") }
        }
    }
}
