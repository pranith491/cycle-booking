const cycles=[
 {name:"Ruby City Cruiser",price:1499,desc:"Comfort-first city rides with a classic silhouette.",icon:"🚲"},
 {name:"Golden Trail MTB",price:2499,desc:"A sturdy all-rounder for weekend adventures.",icon:"🚵"},
 {name:"Velora Road Pro",price:3299,desc:"Light, quick and made for longer road rides.",icon:"🚴"},
 {name:"Classic Heritage",price:1899,desc:"Timeless styling for relaxed neighbourhood rides.",icon:"🚲"},
 {name:"Urban Commuter",price:2199,desc:"A dependable daily ride for your city routine.",icon:"🚴‍♀️"},
 {name:"Junior Star",price:999,desc:"A fun, easy ride for younger cycling fans.",icon:"🚲"}
];
let user=null, mode="login";
const $=id=>document.getElementById(id);
const money=n=>"₹"+Number(n).toLocaleString("en-IN");
function renderCycles(){
 $("cycleGrid").innerHTML=cycles.map((c,i)=>`<article class="cycle-card"><div class="cycle-visual">${c.icon}</div><div class="cycle-info"><h3>${c.name}</h3><p>${c.desc}</p><div class="price-row"><span class="price">${money(c.price)}</span><button class="small-btn" data-cycle="${i}">Book this ↗</button></div></div></article>`).join("");
 $("cycleSelect").innerHTML=cycles.map((c,i)=>`<option value="${i}">${c.name} — ${money(c.price)}</option>`).join("");
 updateTotal();
}
function updateTotal(){const c=cycles[Number($("cycleSelect").value)||0];$("total").textContent=money(c.price*Math.max(1,Number($("quantity").value)||1));}
function openAuth(which="login"){mode=which;syncAuth();$("authModal").classList.remove("hidden");}
function syncAuth(){
 $("authTitle").textContent=mode==="login"?"Login":"Create account";
 $("authSubmit").textContent=mode==="login"?"Login":"Create account";
 $("nameLabel").classList.toggle("hidden",mode==="login");
 $("name").required=mode==="register";
 $("password").autocomplete=mode==="login"?"current-password":"new-password";
 $("loginTab").classList.toggle("active",mode==="login");$("registerTab").classList.toggle("active",mode==="register");$("authMessage").textContent="";
}
async function request(url,options={}){const res=await fetch(url,{headers:{"Content-Type":"application/json"},...options});let data={};try{data=await res.json()}catch{}if(!res.ok)throw new Error(data.message||"Something went wrong.");return data;}
function setUser(u){user=u;localStorage.setItem("veloraUser",JSON.stringify(u));$("accountBtn").textContent=u.name.split(" ")[0]+" · Logout";loadBookings();}
function logout(){user=null;localStorage.removeItem("veloraUser");$("accountBtn").textContent="Login / Sign up";$("myBookings").innerHTML='<p class="muted">Log in to see your bookings.</p>';}
async function loadBookings(){
 if(!user)return;
 try{const data=await request(`/api/bookings/user/${user.id}`);$("myBookings").innerHTML=data.length?data.map(b=>`<div class="booking-item"><div><b>${b.cycleName}</b><small>Booking #${b.id} · ${new Date(b.bookedAt).toLocaleString()}</small><small>${b.quantity} × ${money(b.unitPrice)}</small></div><strong>${money(b.totalPrice)}</strong></div>`).join(""):'<p class="muted">No bookings yet. Choose a cycle above to get started.</p>';}
 catch(e){$("myBookings").innerHTML=`<p class="message">${e.message}</p>`;}
}
$("cycleGrid").addEventListener("click",e=>{const btn=e.target.closest("[data-cycle]");if(!btn)return;$("cycleSelect").value=btn.dataset.cycle;updateTotal();$("booking").scrollIntoView({behavior:"smooth"});});
$("cycleSelect").addEventListener("change",updateTotal);$("quantity").addEventListener("input",updateTotal);
$("accountBtn").addEventListener("click",()=>user?logout():openAuth());
$("closeModal").addEventListener("click",()=>$("authModal").classList.add("hidden"));
$("authModal").addEventListener("click",e=>{if(e.target===$("authModal"))$("authModal").classList.add("hidden");});
$("loginTab").addEventListener("click",()=>{mode="login";syncAuth()});$("registerTab").addEventListener("click",()=>{mode="register";syncAuth()});
$("authForm").addEventListener("submit",async e=>{
 e.preventDefault();$("authMessage").textContent="Please wait…";
 try{const payload={email:$("email").value,password:$("password").value};if(mode==="register")payload.name=$("name").value;
 const u=await request(`/api/auth/${mode==="login"?"login":"register"}`,{method:"POST",body:JSON.stringify(payload)});
 if(mode==="register"){mode="login";syncAuth();$("email").value=payload.email;$("authMessage").textContent="Account created. Please log in.";return;}
 setUser(u);$("authModal").classList.add("hidden");$("bookingMessage").textContent="Welcome back, "+u.name+"!";
 }catch(err){$("authMessage").textContent=err.message;}
});
$("bookingForm").addEventListener("submit",async e=>{
 e.preventDefault();if(!user){openAuth();$("bookingMessage").textContent="Please log in before booking.";return;}
 const c=cycles[Number($("cycleSelect").value)];$("bookingMessage").textContent="Saving booking…";
 try{const b=await request("/api/bookings",{method:"POST",body:JSON.stringify({userId:user.id,cycleName:c.name,quantity:Number($("quantity").value),unitPrice:c.price})});$("bookingMessage").textContent=`Booking confirmed! Booking #${b.id} — ${money(b.totalPrice)}.`;await loadBookings();}
 catch(err){$("bookingMessage").textContent=err.message;}
});
$("refreshBtn").addEventListener("click",loadBookings);
try{const saved=JSON.parse(localStorage.getItem("veloraUser"));if(saved&&saved.id)setUser(saved);}catch{}
renderCycles();
