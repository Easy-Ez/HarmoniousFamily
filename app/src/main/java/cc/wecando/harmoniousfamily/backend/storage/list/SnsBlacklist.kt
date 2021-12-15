package cc.wecando.harmoniousfamily.backend.storage.list

object SnsBlacklist : BaseList<String?>() {
    override operator fun plusAssign(value: String?) {
        if (value == null) {
            return
        }
        super.plusAssign(value.padStart(25, '0'))
    }
}