package co.formaloo.common.exception


class ViewFailure {
    class responseError(msg: String?) : Failure.FeatureFailure(msg)
}
