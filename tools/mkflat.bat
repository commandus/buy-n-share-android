SET fbs=fbs/buynshare.fbs       fbs/fridgemealcards.fbs  fbs/fridges.fbs       fbs/fridgeusers.fbs   fbs/mealcards.fbs fbs/meals.fbs           fbs/purchase.fbs         fbs/user.fbs          fbs/userpurchases.fbs fbs/fridge.fbs fbs/fridgepurchases.fbs fbs/fridgeuser.fbs       fbs/mealcard.fbs      fbs/meal.fbs          fbs/payments.fbs fbs/purchases.fbs       fbs/userfridges.fbs      fbs/users.fbs
SET PATH=%PATH%;D:\l\flatbuffers\Release
flatc --java -o app/gen %fbs%
