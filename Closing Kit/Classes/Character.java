public class Character {
	// HUD elements
	private static int currentHealth = 0;
	private static int currentEnergy = 0;
	private static int currentArmor = 0;

	private static int maxHealth = 0;
	private static int maxArmor = 0;
	private static int maxEnergy = 0;

	private static int currentLevel = 0;
	private static int currentExperience = 0;
	private static int maxExperience = 0;
	private static int currentScore = 0;

	private static int incomingDamage = 0;

	// Movement
	private static int movementSpeed = 0;
	private static int jumpHeight = 0;

	private static int dashDamage = 0;

	// Booleans
	private static boolean pickUpAdded = false;
	private static boolean isShooting = false;
	private static boolean sprintActive = false;
	private static boolean shieldActive = false;
	private static boolean superJumpActive = false;
	private static boolean hoverActive = false;

	public static int currentHealth() {
		int currentHealthValue;

		if (incomingDamage != 0) {
			currentHealthValue = maxHealth - incomingDamage;
		} else {
			currentHealthValue = currentHealth;
		}

		return currentHealthValue;

	}

	public static int healthPercentage() {
		int healthPercent;
		healthPercent = (currentHealth / maxHealth) * 100;

		return healthPercent;

	}

	public static int armorPercentage() {
		int armorPercent;
		armorPercent = (currentArmor / maxArmor) * 100;

		return armorPercent;

	}

	public static int energyPercentage() {
		int energyPercent;
		energyPercent = (currentEnergy / maxEnergy) * 100;

		return energyPercent;

	}
}
