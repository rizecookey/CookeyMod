# CookeyMod

A mod tweaking various smaller things such as adding/changing animations similar to PvP clients.
This mod originated as a PvP client sort-of alternative for the Combat Tests versions of the game, but has been ported to default versions too.

# Current Features
## Animations & Rendering
- <b>Tool blocking</b>: Enables a blocking animation for all tools when blocking with a shield in the other hand, similar to 1.8 sword blocking.
- <b>Swing & use item</b>: Prevents eating/drinking animations from cancelling the swinging animation.
- <b>Camera transition speed</b>: Changes the speed at which the camera height transitions (for example, when sneaking). Can also disable the animation entirely.
- <b>3rd person eating & drinking animation</b>: Enables a third-person eating/drinking animation in third person similar to the old console versions.
- <b>Disable camera shake</b>: Disables the camera shake when bobbing is enabled but not the hand movement itself.
## HUD & Rendering
- <b>Ignore effect FOV</b>: Prevents potion effects from changing your field of view. Sprinting will still slightly increase the FOV.
- <b>Show hand when invisible</b>: Shows a transparent hand when the player has the invisibility effect in first person instead of hiding the hand entirely.
- <b>Damage color on entities</b>: Changes the color of the damage tint for enemies.
- <b>Color hand offset</b>: Allows you to change the height your hand will be at when the cooldown has just reset.
- <b>Damage tint on armor</b>: Enables the damage tint to show on armor as well (similar to how it did in 1.7).
- <b>Alternative bobbing</b>: Enables an alternative bobbing animation (sort-of similar to bedrock).
- <b>Hide unused shield</b>: Hides your shield in first person when it's not used.
- <b>Show hand when invisible</b>: Makes your hand appear in 1st person even if you currently have the invisibility effect.
- <b>Hand opacity when invisible</b>: Changes how transparent your hand is when you are invisible and "Show hand when invisible" is on.
## Miscellaneous
- <b>Fix cooldown desync</b>: Fixes the attack cooldown indicator being out of sync with its actual state on the server in certain cases. (see [MC-218570](https://bugs.mojang.com/browse/MC-218570))
- <b>Name in 3rd person</b>: Shows your own name in third person.
- <b>Mod button in MC options</b>: Changes whether the CookeyMod options button is shown in the general settings.
- <b>Fix local player handling</b>: Fixes a bug in the client that shows particles on the client and resets the player's sprint when an attack on an entity was blocked.
